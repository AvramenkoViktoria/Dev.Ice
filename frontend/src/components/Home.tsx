import React, {useRef} from 'react';
import ProductHeaderButtons from './ProductHeaderButtons';
import ProductFilterSidebar from './ProductFilterSidebar';
import ProductTable, {ProductTableRef} from './ProductTable';
import '../styles/Home.css';

const Home: React.FC = () => {
    const productTableRef = useRef<ProductTableRef>(null);

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
        </div>
    );
};

export default Home;
