import React, {useState} from 'react';
import '../styles/OrderModalA.css';

// Тип для продукту
interface Product {
    name: string;
    qty: number;
    price: number;
    sum: number;
    code: string;
}

// Тип для пропсів компонента
interface OrderModalProps {
    order: {
        products: Product[];
    };
    onClose: () => void;
}

const OrderModal: React.FC<OrderModalProps> = ({order, onClose}) => {
    const [editingIndex, setEditingIndex] = useState<number | null>(null);
    const [tempQty, setTempQty] = useState<string>('');
    const [products, setProducts] = useState<Product[]>(order.products);

    const handleQtyClick = (index: number) => {
        setEditingIndex(index);
        setTempQty(products[index].qty.toString());
    };

    const handleQtyChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTempQty(e.target.value);
    };

    const handleQtyBlur = () => {
        const newQty = parseInt(tempQty);
        if (!isNaN(newQty) && newQty >= 0) {
            const updatedProducts = [...products];
            updatedProducts[editingIndex as number] = {
                ...updatedProducts[editingIndex as number],
                qty: newQty,
                sum: newQty * updatedProducts[editingIndex as number].price,
            };
            setProducts(updatedProducts);
        }
        setEditingIndex(null); // finish editing either way
    };

    return (
        <div className='modal-overlay'>
            <div className='modal-content'>
                <button className='close-btn' onClick={onClose}>
                    Закрити
                </button>
                <div className='product-container'>
                    {products.map((prod, index) => (
                        <div key={index} className='product-block'>
                            <strong>{prod.name}</strong>
                            <p>
                                Кількість:{' '}
                                {editingIndex === index ? (
                                    <input
                                        type='number'
                                        min='0'
                                        autoFocus
                                        value={tempQty}
                                        onChange={handleQtyChange}
                                        onBlur={handleQtyBlur}
                                        style={{width: '60px'}}
                                    />
                                ) : (
                                    <span
                                        onClick={() => handleQtyClick(index)}
                                        style={{
                                            cursor: 'pointer',
                                            color: '#905EF7',
                                        }}
                                    >
                                        {prod.qty}
                                    </span>
                                )}
                            </p>
                            <p>Ціна: {prod.price} грн</p>
                            <p>Сума: {prod.sum} грн</p>
                            <p>
                                <i>Код товару: {prod.code}</i>
                            </p>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default OrderModal;
