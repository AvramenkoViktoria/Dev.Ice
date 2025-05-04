import React from 'react';
import Cookies from 'js-cookie';
import {useNavigate} from 'react-router-dom';
import {deleteCustomer} from '../http/customer';
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
    const email = 'example@email.com';

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

    const handleDeleteAccount = async () => {
        const email = Cookies.get('userEmail');
        if (!email) {
            alert('Неможливо знайти ваш імейл. Будь ласка, спробуйте знову.');
            return;
        }

        const confirmed = window.confirm(
            'Ви впевнені, що хочете видалити акаунт?',
        );
        if (!confirmed) return;

        await handleLogout();

        const success = await deleteCustomer(email);
        if (success) {
            alert('Ваш акаунт було видалено.');
        } else {
            alert('Помилка при видаленні акаунта.');
        }
    };

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
                <button onClick={handleDeleteAccount}>Видалити акаунт</button>
            </div>
        </div>
    );
};

export default ProductHeaderButtons;
