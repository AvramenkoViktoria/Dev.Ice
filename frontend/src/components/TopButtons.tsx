import React from 'react';
import '../styles/TopButtons.css';

// Типи для пропсів
interface TopButtonsProps {
    showDelete: boolean;
    onDelete: () => void;
}

// TopButtons.tsx
interface TopButtonsProps {
    showDelete: boolean;
    onDelete: () => void;
    resetSort: () => void; // Add this
}

const TopButtons: React.FC<TopButtonsProps> = ({showDelete, onDelete}) => {
    return (
        <div className='header-buttons'>
            <span className='manager-label'>МЕНЕДЖЕР</span>
            <div className='button-group'>
                {showDelete && <button onClick={onDelete}>Видалити</button>}
                <button>Оновити список</button>
                <button>Скинути фільтри</button>
                <button>Друкувати звіт</button>
                <button>Вийти</button>
            </div>
        </div>
    );
};

export default TopButtons;
