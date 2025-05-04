import React from 'react';
import {FaShoppingCart} from 'react-icons/fa';
import '../styles/CartButton.css';

interface CartButtonProps {
    onClick: () => void;
}

const CartButton: React.FC<CartButtonProps> = ({onClick}) => {
    return (
        <button className='cart-button' onClick={onClick}>
            <FaShoppingCart size={45} />
        </button>
    );
};

export default CartButton;
