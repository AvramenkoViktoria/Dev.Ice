import React from 'react';
import Cookies from 'js-cookie';
import {useNavigate} from 'react-router-dom';
import '../styles/ProductHeaderButtons.css';
import {deleteManager, getManagerByEmail} from '../http/manager';

interface ProductHeaderButtonsProps {
    fetchProducts: (
        search?: Record<string, string | number | boolean>,
        customFilter?: Record<string, any>,
        sort?: Record<string, 'ASC' | 'DESC'>,
    ) => void;
    resetSorting: () => void;
}

export function getCookie(name: string): string | null {
    const match = document.cookie.match(
        new RegExp('(^| )' + name + '=([^;]+)'),
    );
    return match ? decodeURIComponent(match[2]) : null;
}

const ProductHeaderButtonsM: React.FC<ProductHeaderButtonsProps> = ({
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

        const manager = await getManagerByEmail(email);
        const success = await deleteManager(manager.managerId);
        if (success) {
            alert('Ваш акаунт було видалено.');
        } else {
            alert('Помилка при видаленні акаунта.');
        }
        await handleLogout();
    };

    return (
        <div className='header-buttons'>
            <div className='left-actions'>
                <button onClick={() => fetchProducts()}>
                    Оновити список ⟳
                </button>
                <button onClick={handleResetFilters}>Скинути фільтри ↶</button>
                <button onClick={resetSorting}>Скинути сортування ↶</button>
                <p>МЕНЕДЖЕР</p>
                <button onClick={handleLogout}>ВИЙТИ</button>
            </div>

            <div className='search-bar'>
                <button onClick={handleDeleteAccount}>Видалити акаунт</button>
            </div>
        </div>
    );
};

export default ProductHeaderButtonsM;
