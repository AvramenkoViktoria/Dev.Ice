import React, {
    useEffect,
    useRef,
    useState,
    forwardRef,
    useImperativeHandle,
} from 'react';
import '../styles/ProductTable.css';
import {searchProducts} from '../http/product';
import {SearchPayload} from '../http/filter_sort_search.types';

interface Product {
    product_id: number;
    name: string;
    selling_price: number;
    category: string;
    discount_value: number;
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

const ProductTable = forwardRef<ProductTableRef>((_, ref) => {
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
    const [filter, setFilter] = useState<SearchPayload['filter']>({});
    const dropdownRef = useRef<HTMLDivElement>(null);
    const [sortDirection, setSortDirection] = useState<'ASC' | 'DESC'>('DESC');

    const [lastQuery, setLastQuery] = useState<SearchPayload>({
        search: {},
        filter: {},
        sort: {},
    });

    const fetchProducts = async (
        search: SearchPayload['search'] = {},
        customFilter: SearchPayload['filter'] = {},
        sort: {[key: string]: 'ASC' | 'DESC'} = {},
    ) => {
        const payload: SearchPayload = {
            search,
            filter: customFilter,
            sort,
        };

        setLastQuery(payload);

        const result = await searchProducts(payload);
        if (result) {
            const mapped = result.map(
                (item: any): Product => ({
                    product_id: item.product_id,
                    name: item.name,
                    selling_price: item.selling_price,
                    category: item.category,
                    discount_value: item.discount_value ?? 0,
                    in_stock: item.in_stock,
                    storage_quantity: item.storage_quantity,
                    producer: item.producer,
                    brand: item.brand,
                    inCart: false,
                }),
            );
            setProducts(mapped);
        }
    };

    useImperativeHandle(ref, () => ({
        applyFilter: (newFilter) => {
            const newPayload = {
                ...lastQuery,
                filter: newFilter,
            };
            setFilter(newFilter);
            setLastQuery(newPayload);
            fetchProducts(
                newPayload.search,
                newPayload.filter,
                newPayload.sort,
            );
        },
        refreshProducts: () => {
            fetchProducts(lastQuery.search, lastQuery.filter, lastQuery.sort);
        },
        applySearchPayload: (payload: SearchPayload) => {
            setLastQuery(payload);
            fetchProducts(payload.search, payload.filter, payload.sort);
        },
        getLastQuery: () => lastQuery,

        getCartProducts: () =>
            products
                .filter((p) => p.inCart)
                .map((p) => ({
                    product_id: p.product_id,
                    name: p.name,
                    selling_price: p.selling_price,
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
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const handleCartToggle = (index: number) => {
        const updated = [...products];
        updated[index].inCart = !updated[index].inCart;
        setProducts(updated);
    };

    const handleSearchClick = (column: string) => {
        setVisibleSearchFields((prev) => {
            const isOpen = prev[column];
            const updated = {...prev, [column]: !isOpen};

            if (isOpen) {
                const cleanedInputs = Object.entries(searchInputs).reduce(
                    (acc, [key, val]) => {
                        if (val.trim() !== '' && visibleSearchFields[key]) {
                            acc[key] = parseSearchValue(val);
                        }
                        return acc;
                    },
                    {} as Record<string, string | number | boolean>,
                );
                fetchProducts(cleanedInputs, filter, lastQuery.sort);
            }

            return updated;
        });
    };

    const handleDropdownSelect = (columnKey: string, value: string) => {
        const parsedValue = parseSearchValue(value);
        const search: SearchPayload['search'] = {[columnKey]: parsedValue};
        fetchProducts(search, filter, lastQuery.sort);
        setActiveDropdownKey(null);
    };

    const handleInputChange = (columnKey: string, value: string) => {
        setSearchInputs((prev) => ({...prev, [columnKey]: value}));
    };

    const parseSearchValue = (value: string): string | number | boolean => {
        if (!isNaN(Number(value))) return Number(value);
        if (value.toLowerCase() === 'true') return true;
        if (value.toLowerCase() === 'false') return false;
        return value;
    };

    const renderHeaderCell = (label: string, columnKey: string) => {
        const isDropdown = columnKey === 'category' || columnKey === 'in_stock';
        const isPriceColumn = columnKey === 'selling_price';

        return (
            <th
                key={columnKey}
                style={{position: 'relative', whiteSpace: 'nowrap'}}
            >
                {label}{' '}
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
                            setActiveDropdownKey(columnKey);
                        }}
                    >
                        ▼
                    </button>
                ) : (
                    <>
                        <button onClick={() => handleSearchClick(columnKey)}>
                            ⌕
                        </button>
                        {visibleSearchFields[columnKey] && (
                            <input
                                type='text'
                                style={{width: '140px', marginLeft: '6px'}}
                                value={searchInputs[columnKey] || ''}
                                onChange={(e) =>
                                    handleInputChange(columnKey, e.target.value)
                                }
                            />
                        )}
                    </>
                )}
                {isPriceColumn && (
                    <button
                        style={{marginLeft: '3px'}}
                        onClick={() => {
                            const newDirection: 'ASC' | 'DESC' =
                                sortDirection === 'ASC' ? 'DESC' : 'ASC';
                            setSortDirection(newDirection);

                            const updatedSort: {[key: string]: 'ASC' | 'DESC'} =
                                {
                                    ...lastQuery.sort,
                                    selling_price: newDirection,
                                };

                            fetchProducts(
                                lastQuery.search,
                                lastQuery.filter,
                                updatedSort,
                            );
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
            <div className='table-scroll'>
                <table className='product-table'>
                    <thead>
                        <tr>
                            {renderHeaderCell('Артикул', 'product_id')}
                            {renderHeaderCell('Назва', 'name')}
                            {renderHeaderCell('Ціна', 'selling_price')}
                            {renderHeaderCell('Категорія', 'category')}
                            {renderHeaderCell('Знижка', 'discount_value')}
                            {renderHeaderCell('В наявності', 'in_stock')}
                            {renderHeaderCell('Постачальник', 'producer')}
                            {renderHeaderCell('Бренд', 'brand')}
                            <th>У кошику</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.map((p, i) => (
                            <tr key={p.product_id}>
                                <td>{p.product_id}</td>
                                <td>{p.name}</td>
                                <td>{p.selling_price}</td>
                                <td>{p.category}</td>
                                <td>{p.discount_value + '%'}</td>
                                <td>{p.in_stock ? '✔️' : '✖️'}</td>
                                <td>{p.producer}</td>
                                <td>{p.brand}</td>
                                <td>
                                    <input
                                        type='checkbox'
                                        checked={p.inCart}
                                        onChange={() => handleCartToggle(i)}
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
                            onClick={() =>
                                handleDropdownSelect(activeDropdownKey, option)
                            }
                            className='dropdown-option'
                            style={{padding: '8px', cursor: 'pointer'}}
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

export default ProductTable;
