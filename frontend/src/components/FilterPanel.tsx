import React from 'react';
import '../styles/FilterPanel.css';

const FilterPanel: React.FC = () => {
    return (
        <div className='order-filter-sidebar'>
            <h3>Фільтри</h3>

            <div>
                <label>Менеджер</label>
                <div>
                    <input type='checkbox' /> Катерина
                </div>
                <div>
                    <input type='checkbox' /> Валентина
                </div>
                <div>
                    <input type='checkbox' /> Олег
                </div>
            </div>

            <div>
                <label>Статус</label>
                <div>
                    <input type='checkbox' /> Нове
                </div>
                <div>
                    <input type='checkbox' /> В обробці
                </div>
                <div>
                    <input type='checkbox' /> Відправлено
                </div>
                <div>
                    <input type='checkbox' /> Завершено
                </div>
                <div>
                    <input type='checkbox' /> Повернено
                </div>
            </div>

            <div>
                <label>Сума</label>
                <input type='text' placeholder='від' />
                <input type='text' placeholder='до' />
            </div>

            <div>
                <label>Сплачено</label>
                <div>
                    <input type='checkbox' /> Сплачено
                </div>
                <div>
                    <input type='checkbox' /> Не сплачено
                </div>
            </div>

            <div>
                <label>Дата оформлення</label>
                <input type='date' />
                <input type='date' />
            </div>

            <div>
                <label>Дата відправки</label>
                <input type='date' />
                <input type='date' />
                <div>
                    <input type='checkbox' /> Без дати відправки
                </div>
            </div>

            <div>
                <label>Пошта</label>
                <div>
                    <input type='checkbox' /> Нова Пошта
                </div>
                <div>
                    <input type='checkbox' /> УкрПошта
                </div>
                <div>
                    <input type='checkbox' /> Meest
                </div>
            </div>

            <button>Save</button>
        </div>
    );
};

export default FilterPanel;
