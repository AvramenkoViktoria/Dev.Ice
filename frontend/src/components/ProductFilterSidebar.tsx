import {useState} from 'react';
import '../styles/ProductFilterSidebar.css';
import {FilterValue} from '../http/filter_sort_search.types';

interface ProductFilterSidebarProps {
    onApplyFilter: (payload: {[key: string]: FilterValue}) => void;
}

const ProductFilterSidebar: React.FC<ProductFilterSidebarProps> = ({
    onApplyFilter,
}) => {
    const [category, setCategory] = useState<string[]>([]);
    const [minPrice, setMinPrice] = useState<string>('');
    const [maxPrice, setMaxPrice] = useState<string>('');
    const [minSale, setMinSale] = useState<string>('');
    const [maxSale, setMaxSale] = useState<string>('');
    const [inStock, setInStock] = useState<boolean[]>([]);
    const [minStock, setMinStock] = useState<string>('');
    const [maxStock, setMaxStock] = useState<string>('');
    const [error, setError] = useState<string>('');

    const handleCheckboxChange = <T,>(
        value: T,
        state: T[],
        setState: React.Dispatch<React.SetStateAction<T[]>>,
    ) => {
        setState((prev) =>
            prev.includes(value)
                ? prev.filter((item) => item !== value)
                : [...prev, value],
        );
    };

    const isValidRange = (min: string, max: string): boolean => {
        if (min && max) return Number(min) <= Number(max);
        return true;
    };

    const generateFilterObject = (): {[key: string]: FilterValue} | void => {
        setError('');
        const filter: {[key: string]: FilterValue} = {};

        if (!isValidRange(minPrice, maxPrice)) {
            setError('Мінімальна ціна не може бути більшою за максимальну');
            return;
        }
        if (!isValidRange(minSale, maxSale)) {
            setError('Мінімальна знижка не може бути більшою за максимальну');
            return;
        }
        if (!isValidRange(minStock, maxStock)) {
            setError(
                'Мінімальна кількість на складі не може бути більшою за максимальну',
            );
            return;
        }

        if (category.length) filter['category'] = category;
        if (minPrice || maxPrice) {
            filter['selling_price'] = {
                from: minPrice ? Number(minPrice) : undefined,
                to: maxPrice ? Number(maxPrice) : undefined,
            };
        }
        if (minSale || maxSale) {
            filter['sale'] = {
                from: minSale ? Number(minSale) : undefined,
                to: maxSale ? Number(maxSale) : undefined,
            };
        }
        if (inStock.length) filter['in_stock'] = inStock;
        if (minStock || maxStock) {
            filter['storage_quantity'] = {
                from: minStock ? Number(minStock) : undefined,
                to: maxStock ? Number(maxStock) : undefined,
            };
        }

        return filter;
    };

    return (
        <div className='filter-sidebar'>
            <h3>Фільтри</h3>
            {error && (
                <div style={{color: 'red', marginBottom: '10px'}}>{error}</div>
            )}

            <div>
                <label>Категорія</label>
                <div className='filter-checkbox-group'>
                    {[
                        'Office',
                        'Gaming',
                        'Design',
                        'Ultralight',
                        'Education',
                    ].map((cat) => (
                        <div key={cat}>
                            <input
                                type='checkbox'
                                onChange={() =>
                                    handleCheckboxChange(
                                        cat,
                                        category,
                                        setCategory,
                                    )
                                }
                                checked={category.includes(cat)}
                            />{' '}
                            {cat}
                        </div>
                    ))}
                </div>
            </div>

            <div>
                <label>Ціна (грн)</label>
                <div className='filter-range-group'>
                    <input
                        type='number'
                        placeholder='від'
                        min='0'
                        value={minPrice}
                        onChange={(e) => setMinPrice(e.target.value)}
                    />
                    <input
                        type='number'
                        placeholder='до'
                        min='0'
                        value={maxPrice}
                        onChange={(e) => setMaxPrice(e.target.value)}
                    />
                </div>
            </div>

            <div>
                <label>Знижка (%)</label>
                <div className='filter-range-group'>
                    <input
                        type='number'
                        placeholder='від'
                        min='0'
                        max='100'
                        value={minSale}
                        onChange={(e) => setMinSale(e.target.value)}
                    />
                    <input
                        type='number'
                        placeholder='до'
                        min='0'
                        max='100'
                        value={maxSale}
                        onChange={(e) => setMaxSale(e.target.value)}
                    />
                </div>
            </div>

            <div>
                <label>В наявності</label>
                <div className='filter-checkbox-group'>
                    <div>
                        <input
                            type='checkbox'
                            onChange={() =>
                                handleCheckboxChange(true, inStock, setInStock)
                            }
                            checked={inStock.includes(true)}
                        />{' '}
                        Є
                    </div>
                    <div>
                        <input
                            type='checkbox'
                            onChange={() =>
                                handleCheckboxChange(false, inStock, setInStock)
                            }
                            checked={inStock.includes(false)}
                        />{' '}
                        Немає
                    </div>
                </div>
            </div>

            <div>
                <label>Кількість на складі</label>
                <div className='filter-range-group'>
                    <input
                        type='number'
                        placeholder='від'
                        min='0'
                        value={minStock}
                        onChange={(e) => setMinStock(e.target.value)}
                    />
                    <input
                        type='number'
                        placeholder='до'
                        min='0'
                        value={maxStock}
                        onChange={(e) => setMaxStock(e.target.value)}
                    />
                </div>
            </div>

            <button
                onClick={() => {
                    const result = generateFilterObject();
                    if (result) onApplyFilter(result);
                }}
            >
                Застосувати фільтри
            </button>
        </div>
    );
};

export default ProductFilterSidebar;
