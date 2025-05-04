import '../styles/ProductHeaderButtons.css';

const HeaderButtons = () => {
    return (
        <div className='header-buttons'>
            <div className='left-actions'>
                <button>Оновити список ⟳</button>
                <button>Скинути фільтри ↶</button>
                <button>Скинути сортування ↶</button>
                <p>ПОКУПЕЦЬ</p>
                <button>ВИЙТИ</button>
            </div>

            <div className='search-bar'>
                <input type='text' placeholder='Пошук...' />
                <button>🔍</button>
            </div>
        </div>
    );
};

export default HeaderButtons;
