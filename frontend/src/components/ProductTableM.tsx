import React, {
    useEffect,
    useRef,
    useState,
    forwardRef,
    useImperativeHandle,
} from 'react';
import '../styles/ProductTable.css';
import {searchProducts, deleteProduct, updateProduct} from '../http/product';
import {SearchPayload} from '../http/filter_sort_search.types';

interface Product {
    product_id: number;
    name: string;
    selling_price: number;
    purchase_price: number;
    category: string;
    sale_id: string;
    in_stock: boolean;
    storage_quantity: number;
    producer: string;
    brand: string;
    inCart: boolean;
}

export interface ProductTableRef {
    applyFilter: (filter: SearchPayload['filter']) => void;
    refreshProducts: () => void;
    applySearchPayload: (payload: SearchPayload) => void;
    getLastQuery: () => SearchPayload;
    getCartProducts: () => {
        product_id: number;
        name: string;
        selling_price: number;
        quantity: number;
    }[];
}

const ProductTableM = forwardRef<ProductTableRef>((_, ref) => {
    const [products, setProducts] = useState<Product[]>([]);
    const [searchInputs, setSearchInputs] = useState<Record<string, string>>(
        {},
    );
    const [visibleSearchFields, setVisibleSearchFields] = useState<
        Record<string, boolean>
    >({});
    const [activeDropdownKey, setActiveDropdownKey] = useState<string | null>(
        null,
    );
    const [dropdownPosition, setDropdownPosition] = useState<{
        top: number;
        left: number;
    } | null>(null);
    const [editingCell, setEditingCell] = useState<{
        rowIndex: number;
        field: keyof Product | null;
    } | null>(null);
    const [filter, setFilter] = useState<SearchPayload['filter']>({});
    const [sortDirection, setSortDirection] = useState<'ASC' | 'DESC'>('DESC');
    const [lastQuery, setLastQuery] = useState<SearchPayload>({
        search: {},
        filter: {},
        sort: {},
    });

    const dropdownRef = useRef<HTMLDivElement>(null);

    const fetchProducts = async (
        search: SearchPayload['search'] = {},
        customFilter: SearchPayload['filter'] = {},
        sort: {[key: string]: 'ASC' | 'DESC'} = {},
    ) => {
        const payload: SearchPayload = {search, filter: customFilter, sort};
        setLastQuery(payload);
        const result = await searchProducts(payload);
        if (result) {
            const mapped: Product[] = result.map((item: any) => ({
                ...item,
                sale_id: item.sale_id ?? '',
                inCart: false,
            }));
            setProducts(mapped);
        }
    };

    useImperativeHandle(ref, () => ({
        applyFilter: (newFilter) => {
            setFilter(newFilter);
            fetchProducts(lastQuery.search, newFilter, lastQuery.sort);
        },
        refreshProducts: () => {
            fetchProducts(lastQuery.search, lastQuery.filter, lastQuery.sort);
        },
        applySearchPayload: (payload) => {
            setLastQuery(payload);
            fetchProducts(payload.search, payload.filter, payload.sort);
        },
        getLastQuery: () => lastQuery,
        getCartProducts: () =>
            products
                .filter((p) => p.inCart)
                .map(({product_id, name, selling_price}) => ({
                    product_id,
                    name,
                    selling_price,
                    quantity: 1,
                })),
    }));

    useEffect(() => {
        fetchProducts();
    }, []);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (
                dropdownRef.current &&
                !dropdownRef.current.contains(event.target as Node)
            ) {
                setActiveDropdownKey(null);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () =>
            document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const toggleCartItem = (index: number) => {
        const updated = [...products];
        updated[index].inCart = !updated[index].inCart;
        setProducts(updated);
    };

    const removeCartItems = async () => {
        const toDelete = products.filter((p) => p.inCart);
        const failedIds: number[] = [];

        await Promise.all(
            toDelete.map(async (p) => {
                try {
                    const response = await deleteProduct(p.product_id);
                    if (
                        response.includes(
                            'because it is referenced in order_product',
                        )
                    )
                        failedIds.push(p.product_id);
                } catch {
                    failedIds.push(p.product_id);
                }
            }),
        );

        if (failedIds.length > 0) {
            alert(
                `Не вдалось видалити товари з ID: ${failedIds.join(
                    ', ',
                )}, оскільки вони містяться в замовленнях`,
            );
        }

        const updated = products.filter(
            (p) => !p.inCart || failedIds.includes(p.product_id),
        );
        setProducts(updated);
    };

    const handleSearchToggle = (column: string) => {
        setVisibleSearchFields((prev) => {
            const updated = {...prev, [column]: !prev[column]};
            if (prev[column]) {
                const search: Record<string, string | number | boolean> = {};
                for (const key in searchInputs) {
                    if (searchInputs[key].trim() && updated[key]) {
                        search[key] = parseValue(searchInputs[key]);
                    }
                }
                fetchProducts(search, filter, lastQuery.sort);
            }
            return updated;
        });
    };

    const handleDropdownSelect = (columnKey: string, value: string) => {
        const parsed = parseValue(value);
        fetchProducts({[columnKey]: parsed}, filter, lastQuery.sort);
        setActiveDropdownKey(null);
    };

    const parseValue = (val: string): string | number | boolean => {
        if (!isNaN(Number(val))) return Number(val);
        const lowered = val.toLowerCase();
        if (lowered === 'true') return true;
        if (lowered === 'false') return false;
        return val;
    };

    const renderEditableCell = (
        value: string | number,
        rowIndex: number,
        field: keyof Product,
    ) => {
        const isEditing =
            editingCell?.rowIndex === rowIndex && editingCell.field === field;

        const handleBlur = async (e: React.FocusEvent<HTMLInputElement>) => {
            const updatedValue = e.target.value;

            setEditingCell(null);

            if (field === 'purchase_price' && isNaN(Number(updatedValue))) {
                console.warn(`Invalid number: ${updatedValue}`);
                return;
            }

            setProducts((prev) => {
                const updated = [...prev];
                const oldProduct = updated[rowIndex];

                const newValue =
                    field === 'purchase_price'
                        ? Number(updatedValue)
                        : updatedValue;

                updated[rowIndex] = {
                    ...oldProduct,
                    [field]: newValue,
                };

                const productForUpdate = {
                    productId: oldProduct.product_id,
                    saleId: parseInt(oldProduct.sale_id || '0'),
                    name: updated[rowIndex].name,
                    sellingPrice: updated[rowIndex].selling_price,
                    purchasePrice: updated[rowIndex].purchase_price,
                    category: updated[rowIndex].category,
                    inStock: updated[rowIndex].in_stock,
                    storageQuantity: updated[rowIndex].storage_quantity,
                    producer: updated[rowIndex].producer,
                    brand: updated[rowIndex].brand,
                };

                updateProduct(oldProduct.product_id, productForUpdate).catch(
                    (err) => {
                        console.error('Update failed:', err);
                    },
                );

                return updated;
            });
        };

        return (
            <td
                onClick={() => setEditingCell({rowIndex, field})}
                style={{cursor: 'pointer'}}
            >
                {isEditing ? (
                    <input
                        type='text'
                        autoFocus
                        defaultValue={value}
                        onBlur={handleBlur}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                                (e.target as HTMLInputElement).blur();
                            }
                        }}
                    />
                ) : (
                    value
                )}
            </td>
        );
    };

    const renderHeader = (label: string, key: string) => {
        const isDropdown = ['category', 'in_stock'].includes(key);
        const isSortable = key === 'selling_price';
        return (
            <th key={key} style={{position: 'relative', whiteSpace: 'nowrap'}}>
                {label}
                {isDropdown ? (
                    <button
                        onClick={(e) => {
                            const rect = (
                                e.target as HTMLElement
                            ).getBoundingClientRect();
                            setDropdownPosition({
                                top: rect.bottom,
                                left: rect.left,
                            });
                            setActiveDropdownKey(key);
                        }}
                    >
                        ▼
                    </button>
                ) : (
                    <>
                        <button onClick={() => handleSearchToggle(key)}>
                            ⌕
                        </button>
                        {visibleSearchFields[key] && (
                            <input
                                type='text'
                                value={searchInputs[key] || ''}
                                onChange={(e) =>
                                    setSearchInputs((prev) => ({
                                        ...prev,
                                        [key]: e.target.value,
                                    }))
                                }
                                style={{width: '140px', marginLeft: '6px'}}
                            />
                        )}
                    </>
                )}
                {isSortable && (
                    <button
                        onClick={() => {
                            const newDir =
                                sortDirection === 'ASC' ? 'DESC' : 'ASC';
                            setSortDirection(newDir);
                            fetchProducts(lastQuery.search, lastQuery.filter, {
                                ...lastQuery.sort,
                                selling_price: newDir,
                            });
                        }}
                    >
                        {sortDirection === 'ASC' ? '↑' : '↓'}
                    </button>
                )}
            </th>
        );
    };

    const getOptions = (key: string): string[] =>
        key === 'category'
            ? ['Office', 'Gaming', 'Design', 'Ultralight', 'Education']
            : ['true', 'false'];

    return (
        <div className='product-table-container'>
            <p>Знайдено {products.length} товарів</p>

            {products.some((p) => p.inCart) && (
                <button
                    onClick={removeCartItems}
                    className='delete-selected-btn'
                >
                    Видалити вибрані товари
                </button>
            )}

            <div className='table-scroll'>
                <table className='product-table'>
                    <thead>
                        <tr>
                            {renderHeader('Артикул', 'product_id')}
                            {renderHeader('Назва', 'name')}
                            {renderHeader('Ціна продажу', 'selling_price')}
                            {renderHeader('Ціна закупу', 'purchase_price')}
                            {renderHeader('Категорія', 'category')}
                            {renderHeader('Знижка', 'sale_id')}
                            {renderHeader('В наявності', 'in_stock')}
                            {renderHeader('Постачальник', 'producer')}
                            {renderHeader('Бренд', 'brand')}
                            <th>Обране</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.map((p, i) => (
                            <tr key={p.product_id}>
                                <td>{p.product_id}</td>
                                {renderEditableCell(p.name, i, 'name')}
                                <td>{p.selling_price}</td>
                                {renderEditableCell(
                                    p.purchase_price,
                                    i,
                                    'purchase_price',
                                )}
                                <td>{p.category}</td>
                                <td>{p.sale_id}</td>
                                <td>{p.in_stock ? '✔️' : '✖️'}</td>
                                {renderEditableCell(p.producer, i, 'producer')}
                                {renderEditableCell(p.brand, i, 'brand')}
                                <td>
                                    <input
                                        type='checkbox'
                                        checked={p.inCart}
                                        onChange={() => toggleCartItem(i)}
                                    />
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {activeDropdownKey && dropdownPosition && (
                <div
                    className='dropdown-menu'
                    ref={dropdownRef}
                    style={{
                        position: 'fixed',
                        top: dropdownPosition.top,
                        left: dropdownPosition.left,
                        backgroundColor: 'white',
                        border: '1px solid #ccc',
                        zIndex: 1000,
                        width: '140px',
                        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
                    }}
                >
                    {getOptions(activeDropdownKey).map((option) => (
                        <div
                            key={option}
                            className='dropdown-option'
                            style={{padding: '8px', cursor: 'pointer'}}
                            onClick={() =>
                                handleDropdownSelect(activeDropdownKey, option)
                            }
                        >
                            {activeDropdownKey === 'in_stock'
                                ? option === 'true'
                                    ? 'Так'
                                    : 'Ні'
                                : option}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
});

export default ProductTableM;
