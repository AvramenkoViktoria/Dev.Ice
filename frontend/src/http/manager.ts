import {Manager} from './dto';

const BASE_URL = 'http://localhost:8080/api/managers';

/**
 * Sends a request to add a new manager.
 * @param manager The manager data to be added.
 * @returns A promise resolving to the response message or throws an error on failure.
 */
export async function addManager(manager: Manager): Promise<string> {
    try {
        const response = await fetch(`${BASE_URL}/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(manager),
        });

        const rawBody = await response.text();

        if (!response.ok) {
            console.error(`HTTP ${response.status}: ${rawBody}`);
            throw new Error(`HTTP ${response.status}: ${rawBody}`);
        }

        try {
            return JSON.parse(rawBody);
        } catch {
            return rawBody;
        }
    } catch (error) {
        console.error('Failed to add manager:', error);
        throw error;
    }
}

/**
 * Sends a request to update an existing manager.
 * @param manager The updated manager data.
 * @returns A promise resolving to the response message or throws an error on failure.
 */
export async function updateManager(manager: Manager): Promise<string> {
    try {
        const response = await fetch(`${BASE_URL}/update`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(manager),
        });

        const responseBody = await response.text();

        if (!response.ok) {
            throw new Error(responseBody || response.statusText);
        }

        return responseBody;
    } catch (error: any) {
        console.error('Failed to update manager:', error.message || error);
        throw error;
    }
}

/**
 * Sends a request to delete a manager by ID.
 * @param id The ID of the manager to be deleted.
 * @returns A promise resolving to the response message or throws an error on failure.
 */
export async function deleteManager(id: number): Promise<string> {
    try {
        const response = await fetch(`${BASE_URL}/${id}`, {
            method: 'DELETE',
            credentials: 'include',
        });

        if (!response.ok) throw new Error(response.statusText);

        const message = await response.text();
        return message;
    } catch (error) {
        console.error('Failed to delete manager:', error);
        throw error;
    }
}

export async function getManagerById(id: number): Promise<any> {
    try {
        const response = await fetch(`${BASE_URL}/${id}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                credentials: 'include',
            },
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || response.statusText);
        }

        const manager = await response.json();
        return manager;
    } catch (error) {
        console.error('Failed to fetch manager:', error);
        throw error;
    }
}

export async function getManagerByEmail(email: string): Promise<Manager> {
    try {
        const response = await fetch(
            `${BASE_URL}/email/${encodeURIComponent(email)}`,
            {
                method: 'GET',
                credentials: 'include',
            },
        );

        if (!response.ok) throw new Error(response.statusText);

        const manager = await response.json();
        return manager;
    } catch (error) {
        console.error('Failed to fetch manager by email:', error);
        throw error;
    }
}

/**
 * Sends a request to retrieve all managers.
 * @returns A promise resolving to a list of all managers, or throws an error on failure.
 */
export async function getAllManagers(): Promise<Manager[]> {
    try {
        const response = await fetch(BASE_URL);
        if (!response.ok) throw new Error(response.statusText);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Failed to fetch managers:', error);
        throw error;
    }
}

/**
 * Sends a request to get product sales statistics for a manager based on a JSON input.
 * @param jsonInput The JSON string containing managerId and date range.
 * @returns A promise resolving to a list of product sales data, or throws an error on failure.
 */
export async function getManagerProductSales(
    jsonInput: string,
): Promise<any[]> {
    try {
        const response = await fetch(`${BASE_URL}/sales-statistics`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: jsonInput,
        });

        if (!response.ok) throw new Error(response.statusText);

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Failed to fetch product sales statistics:', error);
        throw error;
    }
}
