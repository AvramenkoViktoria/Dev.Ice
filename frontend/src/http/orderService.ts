import {Order} from './types';

export const fetchOrders = async (): Promise<Order[]> => {
    const response = await fetch('/api/orders');
    if (!response.ok) {
        throw new Error('Failed to fetch orders');
    }
    return response.json();
};
