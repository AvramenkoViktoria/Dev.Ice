import {Customer} from './dto';

const BASE_URL = 'http://localhost:8080/api/customers';

export const createCustomer = async (customer: Customer): Promise<boolean> => {
    try {
        const response = await fetch(BASE_URL, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(customer),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return true;
    } catch (error) {
        console.error('Failed to create customer:', error);
        return false;
    }
};

export const updateCustomer = async (customer: Customer): Promise<boolean> => {
    try {
        const response = await fetch(BASE_URL, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(customer),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return true;
    } catch (error) {
        console.error('Failed to update customer:', error);
        return false;
    }
};

export const deleteCustomer = async (email: string): Promise<boolean> => {
    try {
        const response = await fetch(
            `${BASE_URL}/${encodeURIComponent(email)}`,
            {
                method: 'DELETE',
            },
        );

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return true;
    } catch (error) {
        console.error('Failed to delete customer:', error);
        return false;
    }
};

export const getAllCustomers = async (): Promise<Customer[] | null> => {
    try {
        const response = await fetch(BASE_URL);

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Failed to fetch all customers:', error);
        return null;
    }
};

export const getCustomerByEmail = async (
    email: string,
): Promise<Customer | null> => {
    try {
        const response = await fetch(
            `${BASE_URL}/${encodeURIComponent(email)}`,
        );

        if (!response.ok) {
            if (response.status === 404) {
                return null;
            }
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Failed to fetch customer with email ${email}:`, error);
        return null;
    }
};
