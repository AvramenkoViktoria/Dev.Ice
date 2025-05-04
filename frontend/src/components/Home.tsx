import React, {useRef, useState} from 'react';
import ProductHeaderButtons from './ProductHeaderButtons';
import ProductFilterSidebar from './ProductFilterSidebar';
import ProductTable, {ProductTableRef} from './ProductTable';
import CartButton from './CartButton';
import OrderModal from './OrderModal';
import '../styles/Home.css';

interface CartItem {
    product_id: number;
    name: string;
    selling_price: number;
    quantity: number;
}

const Home: React.FC = () => {
    const productTableRef = useRef<ProductTableRef>(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [cartItems, setCartItems] = useState<CartItem[]>([]);

    const handleCartClick = () => {
        const current = productTableRef.current;
        if (current) {
            const productsInCart = current.getCartProducts?.();
            if (productsInCart && productsInCart.length > 0) {
                setCartItems(productsInCart);
                setIsModalOpen(true);
            } else {
                alert('Кошик порожній!');
            }
        }
    };

    const handleResetSorting = () => {
        const current = productTableRef.current;
        if (current) {
            const lastQuery = current.getLastQuery?.();
            if (lastQuery) {
                current.applySearchPayload({
                    ...lastQuery,
                    sort: {product_id: 'ASC'},
                });
            }
        }
    };

    return (
        <div className='home-container'>
            <ProductHeaderButtons
                fetchProducts={() => productTableRef.current?.refreshProducts()}
                resetSorting={handleResetSorting}
            />
            <div className='main-content'>
                <ProductFilterSidebar
                    onApplyFilter={(newFilter) => {
                        productTableRef.current?.applyFilter(newFilter);
                    }}
                />
                <ProductTable ref={productTableRef} />
            </div>
            <CartButton onClick={handleCartClick} />
            {isModalOpen && (
                <OrderModal
                    products={cartItems}
                    onClose={() => setIsModalOpen(false)}
                />
            )}
        </div>
    );
};

export default Home;
