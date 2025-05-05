import {
    OrderRequestDto,
    OrderUpdateRequestDto,
    Order,
    OrderProduct,
} from './dto';
import {SearchPayload} from './filter_sort_search.types';

/**
 * Sends a search request for orders using filters, sorting, and search payload.
 * @param searchPayload Payload containing filter, search, and sort parameters.
 * @returns An array of orders or null if the request fails.
 */
export const searchOrders = async (
    searchPayload: SearchPayload,
): Promise<any[] | null> => {
    const url = 'http://localhost:8080/api/orders/search';

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(searchPayload),
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Failed to fetch search results:', error);
        return null;
    }
};

/**
 * Sends a request to add a new order.
 * @param orderRequest Order data to be created.
 * @returns The created order object, or throws an error on failure.
 */
export async function addOrder(orderRequest: OrderRequestDto): Promise<any> {
    const url = 'http://localhost:8080/api/orders/add';

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(orderRequest),
            credentials: 'include',
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
}

/**
 * Sends a request to update an existing order.
 * @param updateRequest Order update data.
 */
export async function updateOrder(
    updateRequest: OrderUpdateRequestDto,
): Promise<void> {
    const url = 'http://localhost:8080/api/orders/update';

    try {
        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updateRequest),
            credentials: 'include',
        });

        const text = await response.text();

        if (!response.ok) {
            console.error(`Server responded with ${response.status}: ${text}`);
            throw new Error('Failed to update order');
        }

        console.log('Order updated successfully:', text);
    } catch (error: unknown) {
        if (error instanceof Error) {
            console.error('Error while updating order:', error.message);
        } else {
            console.error('Unknown error while updating order:', error);
        }
    }
}

export async function getOrderById(
    orderId: number,
): Promise<OrderUpdateRequestDto> {
    const url = `http://localhost:8080/api/orders/get/${orderId}`;

    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                Accept: 'application/json',
            },
            credentials: 'include',
        });

        if (!response.ok) {
            const text = await response.text();
            console.error(`Server responded with ${response.status}: ${text}`);
            throw new Error('Failed to retrieve order');
        }

        const order: OrderUpdateRequestDto = await response.json();
        console.log('Order retrieved successfully:', order);
        return order;
    } catch (error: unknown) {
        if (error instanceof Error) {
            console.error('Error while retrieving order:', error.message);
            throw error;
        } else {
            console.error('Unknown error while retrieving order:', error);
            throw new Error('Unknown error');
        }
    }
}

/**
 * Sends a request to delete an order by its ID.
 * @param id ID of the order to delete.
 */
async function deleteOrder(id: number): Promise<void> {
    const url = `http://localhost:8080/api/orders/${id}`;

    try {
        const response = await fetch(url, {
            method: 'DELETE',
        });

        const text = await response.text();

        if (!response.ok) {
            console.error(
                `Failed to delete order (${response.status}): ${text}`,
            );
            throw new Error('Order deletion failed');
        }

        console.log('Order deleted successfully:', text);
    } catch (error) {
        console.error('Error during order deletion:', error);
    }
}

const updatedOrder: Order = {
    orderId: 11,
    managerId: 2,
    customerEmail: 'rodschneider@gmail.com',
    status: 'PROCESSING',
    placementDate: new Date('2024-12-01').toISOString(),
    dispatchDate: new Date().toISOString(),
    paymentMethod: 'CASH',
    payed: true,
    post: 'Ukrposhta',
    postOffice: 'Lviv, Branch 12',
    orderAmount: 2222.99,
};

const updatedProducts: OrderProduct[] = [
    {orderId: 42, productId: 1, number: 1},
    {orderId: 42, productId: 2, number: 3},
];

const updateRequest: OrderUpdateRequestDto = {
    order: updatedOrder,
    orderProducts: updatedProducts,
};

// updateOrder(updateRequest);
// deleteOrder(11);
