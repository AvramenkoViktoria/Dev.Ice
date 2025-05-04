import ProductHeaderButtons from './ProductHeaderButtons';
import ProductFilterSidebar from './ProductFilterSidebar';
import ProductTable from './ProductTable';
import '../styles/Home.css';

const Home = () => {
    return (
        <div className='home-container'>
            <ProductHeaderButtons />
            <div className='main-content'>
                <ProductFilterSidebar />
                <ProductTable />
            </div>
        </div>
    );
};

export default Home;
