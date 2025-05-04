import '../styles/ProductFilterSidebar.css';

const ProductFilterSidebar = () => {
    return (
        <div className='filter-sidebar'>
            <h3>Фільтри</h3>

            <div>
                <label>Категорія</label>
                <div className='filter-checkbox-group'>
                    <div>
                        <input type='checkbox' /> Офісні
                    </div>
                    <div>
                        <input type='checkbox' /> Ігрові
                    </div>
                    <div>
                        <input type='checkbox' /> Для дизайну
                    </div>
                    <div>
                        <input type='checkbox' /> Ультралегкі
                    </div>
                    <div>
                        <input type='checkbox' /> Для навчання
                    </div>
                </div>
            </div>

            <div>
                <label>Ціна (грн)</label>
                <div className='filter-range-group'>
                    <input type='number' placeholder='від' min='0' />
                    <input type='number' placeholder='до' min='0' />
                </div>
            </div>

            <div>
                <label>Знижка</label>
                <div className='filter-checkbox-group'>
                    <div>
                        <input type='checkbox' /> 0–10%
                    </div>
                    <div>
                        <input type='checkbox' /> 10–30%
                    </div>
                    <div>
                        <input type='checkbox' /> 30–50%
                    </div>
                    <div>
                        <input type='checkbox' /> 50%+
                    </div>
                </div>
            </div>

            <div>
                <label>В наявності</label>
                <div className='filter-checkbox-group'>
                    <div>
                        <input type='checkbox' /> Є
                    </div>
                    <div>
                        <input type='checkbox' /> Немає
                    </div>
                </div>
            </div>

            <div>
                <label>Кількість на складі</label>
                <div className='filter-range-group'>
                    <input type='number' placeholder='від' min='0' />
                    <input type='number' placeholder='до' min='0' />
                </div>
            </div>

            <button>Застосувати фільтри</button>
        </div>
    );
};

export default ProductFilterSidebar;
