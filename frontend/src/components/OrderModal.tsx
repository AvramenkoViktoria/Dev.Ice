import React, {useState} from 'react';
import {FaTimes} from 'react-icons/fa';
import {getCookie} from './ProductHeaderButtons';
import '../styles/OrderModal.css';

interface Product {
    product_id: number;
    name: string;
    selling_price: number;
    quantity: number;
}

interface OrderModalProps {
    products: Product[];
    onClose: () => void;
}

interface OrderRequestDto {
    orderId: number;
    managerId: number | null;
    customerEmail: string;
    status: string;
    placementDate: string;
    dispatchDate: string | null;
    paymentMethod: string;
    payed: boolean;
    post: string;
    postOffice: string;
    orderAmount: number;
    products: {productId: number; number: number}[];
}

const addOrder = async (orderRequest: OrderRequestDto): Promise<any> => {
    const url = 'http://localhost:8080/api/orders/add';

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(orderRequest),
        });

        const text = await response.text();

        if (!response.ok) {
            console.error(`Server responded with ${response.status}: ${text}`);
            throw new Error('Failed to add order');
        }

        try {
            return JSON.parse(text);
        } catch (parseError) {
            console.error('Failed to parse JSON:', parseError);
            throw new Error('Invalid JSON response from server');
        }
    } catch (error) {
        console.error('Error while adding order:', error);
        throw error;
    }
};

const OrderModal: React.FC<OrderModalProps> = ({products, onClose}) => {
    const [cartItems, setCartItems] = useState(products);
    const [payment, setPayment] = useState('CARD');
    const [delivery, setDelivery] = useState('Nova Poshta');
    const [branchNumber, setBranchNumber] = useState('');

    const handleQuantityChange = (id: number, newQuantity: number) => {
        setCartItems((prev) =>
            prev.map((item) =>
                item.product_id === id
                    ? {...item, quantity: newQuantity}
                    : item,
            ),
        );
    };

    const total = cartItems.reduce(
        (acc, item) => acc + item.selling_price * item.quantity,
        0,
    );

    const handleOrderSubmit = async () => {
        const hasInvalidQuantity = cartItems.some((item) => item.quantity <= 0);
        if (hasInvalidQuantity) {
            alert('Кількість кожного товару повинна бути більше 0!');
            return;
        }

        if (!/^\d+$/.test(branchNumber)) {
            alert('Номер відділення має бути цілим числом!');
            return;
        }

        const email = getCookie('userEmail');
        const order: OrderRequestDto = {
            orderId: 0,
            managerId: null,
            customerEmail: email ?? '',
            status: 'NEW',
            placementDate: new Date().toISOString(),
            dispatchDate: null,
            paymentMethod: payment,
            payed: false,
            post: delivery,
            postOffice: branchNumber,
            orderAmount: total,
            products: cartItems.map((item) => ({
                productId: item.product_id,
                number: item.quantity,
            })),
        };

        try {
            await addOrder(order);
            alert('Замовлення оформлено!');
            onClose();
        } catch (err) {
            alert('Помилка при оформленні замовлення. Спробуйте ще раз.');
        }
    };

    return (
        <div className='modal-backdrop'>
            <div className='modal'>
                <button className='close-button' onClick={onClose}>
                    <FaTimes />
                </button>
                <h2>Оформлення замовлення</h2>
                <h3>Список товарів:</h3>
                {cartItems.map((product) => (
                    <div key={product.product_id} className='product-line'>
                        <span>{product.name}</span>
                        <input
                            type='number'
                            min='1'
                            value={product.quantity}
                            onChange={(e) =>
                                handleQuantityChange(
                                    product.product_id,
                                    Number(e.target.value),
                                )
                            }
                        />
                        <span>
                            {product.quantity} x {product.selling_price} грн
                        </span>
                    </div>
                ))}
                <p>
                    <strong>Всього:</strong> {total} грн
                </p>

                <label>Спосіб оплати:</label>
                <select
                    value={payment}
                    onChange={(e) => setPayment(e.target.value)}
                >
                    <option value='CARD'>Картка</option>
                    <option value='CASH'>Готівка</option>
                    <option value='PAYPAL'>PayPal</option>
                </select>

                <label>Пошта:</label>
                <select
                    value={delivery}
                    onChange={(e) => setDelivery(e.target.value)}
                >
                    <option value='Nova Poshta'>Nova Poshta</option>
                    <option value='Ukrposhta'>Ukrposhta</option>
                    <option value='Meest'>Meest</option>
                </select>

                <label>Номер відділення:</label>
                <input
                    type='text'
                    value={branchNumber}
                    onChange={(e) => setBranchNumber(e.target.value)}
                />

                <button onClick={handleOrderSubmit}>Оформити замовлення</button>
            </div>
        </div>
    );
};

export default OrderModal;
