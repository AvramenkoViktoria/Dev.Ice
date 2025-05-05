import React, {useState, useMemo} from 'react';
import FilterPanel from './FilterPanel';
import OrderTable from './OrderTable';
import TopButtons from './TopButtons';
import '../styles/A.css';
import ordersData from './dummyOrders';
import OrderModal from './OrderModalA';
import {Order, Product} from '../http/types';

interface SortConfig {
    key: string | null;
    direction: 'ascending' | 'descending' | null;
}

function Admin() {
    const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
    const [orders, setOrders] = useState<Order[]>(ordersData);
    const [selectedOrderIds, setSelectedOrderIds] = useState<number[]>([]);
    const [sortConfig, setSortConfig] = useState<SortConfig>({
        key: null,
        direction: null,
    });

    const handleDeleteSelected = () => {
        setOrders((prevOrders) =>
            prevOrders.filter((order) => !selectedOrderIds.includes(order.id)),
        );
        setSelectedOrderIds([]);
    };

    const handleSort = (key: string) => {
        let direction: 'ascending' | 'descending' = 'ascending';
        if (sortConfig.key === key && sortConfig.direction === 'ascending') {
            direction = 'descending';
        }
        setSortConfig({key, direction});
    };

    const resetSort = () => {
        setSortConfig({key: null, direction: null});
    };

    const sortedOrders = useMemo(() => {
        if (!sortConfig.key) return orders;
        const sorted = [...orders];
        sorted.sort((a, b) => {
            const aVal = a[sortConfig.key as string];
            const bVal = b[sortConfig.key as string];

            if (aVal < bVal)
                return sortConfig.direction === 'ascending' ? -1 : 1;
            if (aVal > bVal)
                return sortConfig.direction === 'ascending' ? 1 : -1;
            return 0;
        });
        return sorted;
    }, [orders, sortConfig]);

    return (
        <div className='app-container'>
            <button
                className='floating-btn'
                onClick={() => alert('Кнопка натиснута!')}
            ></button>

            <TopButtons
                showDelete={selectedOrderIds.length > 0}
                onDelete={handleDeleteSelected}
                resetSort={resetSort}
            />

            <div className='main-content'>
                <FilterPanel />
                <OrderTable
                    orders={sortedOrders}
                    onOpen={setSelectedOrder}
                    selectedOrderIds={selectedOrderIds}
                    setSelectedOrderIds={setSelectedOrderIds}
                    onSort={handleSort}
                />
            </div>

            {selectedOrder && (
                <OrderModal
                    order={selectedOrder}
                    onClose={() => setSelectedOrder(null)}
                />
            )}
        </div>
    );
}

export default Admin;
