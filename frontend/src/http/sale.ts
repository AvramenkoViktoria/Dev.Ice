import {Sale} from './dto';

const BASE_URL = 'http://localhost:8080/api/sales';

export const addSale = async (sale: Sale): Promise<boolean> => {
    try {
        const response = await fetch(BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(sale),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return true;
    } catch (error) {
        console.error('Failed to add sale:', error);
        return false;
    }
};

export const getAllSales = async (): Promise<Sale[] | null> => {
    try {
        const response = await fetch(BASE_URL);

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Failed to fetch all sales:', error);
        return null;
    }
};

export const updateSale = async (
    saleId: number,
    sale: Sale,
): Promise<boolean> => {
    try {
        const response = await fetch(`${BASE_URL}/${saleId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(sale),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return true;
    } catch (error) {
        console.error('Failed to update sale:', error);
        return false;
    }
};

export const deleteSale = async (saleId: number): Promise<boolean> => {
    try {
        const response = await fetch(`${BASE_URL}/${saleId}`, {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return true;
    } catch (error) {
        console.error('Failed to delete sale:', error);
        return false;
    }
};
