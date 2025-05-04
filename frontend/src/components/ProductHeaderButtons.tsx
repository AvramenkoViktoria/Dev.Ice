import React from 'react';
import {useNavigate} from 'react-router-dom';
import '../styles/ProductHeaderButtons.css';

interface ProductHeaderButtonsProps {
    fetchProducts: (
        search?: Record<string, string | number | boolean>,
        customFilter?: Record<string, any>,
        sort?: Record<string, 'ASC' | 'DESC'>,
    ) => void;
    resetSorting: () => void;
}

const ProductHeaderButtons: React.FC<ProductHeaderButtonsProps> = ({
    fetchProducts,
    resetSorting,
}) => {
    const navigate = useNavigate();

    const handleResetFilters = () => {
        window.location.reload();
    };

    const handleLogout = async () => {
        const csrfToken = getCookie('XSRF-TOKEN');

        await fetch('/logout', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'X-XSRF-TOKEN': csrfToken || '',
            },
        });

        navigate('/');
    };

    function getCookie(name: string): string | null {
        const match = document.cookie.match(
            new RegExp('(^| )' + name + '=([^;]+)'),
        );
        return match ? decodeURIComponent(match[2]) : null;
    }

    return (
        <div className='header-buttons'>
            <div className='left-actions'>
                <button onClick={() => fetchProducts()}>
                    Оновити список ⟳
                </button>
                <button onClick={handleResetFilters}>Скинути фільтри ↶</button>
                <button onClick={resetSorting}>Скинути сортування ↶</button>
                <p>ПОКУПЕЦЬ</p>
                <button onClick={handleLogout}>ВИЙТИ</button>
            </div>

            <div className='search-bar'>
                <input type='text' placeholder='Пошук...' />
                <button>🔍</button>
            </div>
        </div>
    );
};

export default ProductHeaderButtons;
