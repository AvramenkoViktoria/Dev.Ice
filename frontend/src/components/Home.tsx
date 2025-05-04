import {useRef} from 'react';
import ProductHeaderButtons from './ProductHeaderButtons';
import ProductFilterSidebar from './ProductFilterSidebar';
import ProductTable, {ProductTableRef} from './ProductTable';
import '../styles/Home.css';

const Home = () => {
    const productTableRef = useRef<ProductTableRef>(null);

    return (
        <div className='home-container'>
            <ProductHeaderButtons />
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
