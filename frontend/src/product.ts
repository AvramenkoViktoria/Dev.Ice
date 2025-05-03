const BASE_URL = 'http://localhost:8080/api/products';

import {Product} from './dto';
import {SearchPayload} from './filter_sort_search.types';

/**
 * Sends a request to retrieve all products from the backend.
 * @returns A promise resolving to a list of all products, or throws an error on failure.
 */
export async function getAllProducts(): Promise<Product[]> {
    try {
        const response = await fetch(BASE_URL);
        if (!response.ok) throw new Error(response.statusText);
        const data = await response.json();
        console.log('Fetched products:', data);
        return data;
    } catch (error) {
        console.error('Failed to fetch products:', error);
        throw error;
    }
}

/**
 * Sends a POST request to search for products based on the specified search criteria.
 * @param searchPayload The search parameters including filter, search, and sort options.
 * @returns A promise that resolves to an array of matching products, or null if the request fails.
 */
const searchProducts = async (
    searchPayload: SearchPayload,
): Promise<any[] | null> => {
    const url = 'http://localhost:8080/api/products/search';

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(searchPayload),
            redirect: 'manual',
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Failed to fetch product search results:', error);
        return null;
    }
};

/**
 * Sends a request to add a new product.
 * @param product Product data to be created.
 * @returns A promise that resolves when the product is successfully added, or throws an error on failure.
 */
export async function addProduct(product: Product): Promise<void> {
    try {
        const response = await fetch(BASE_URL, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(product),
        });
        const result = await response.text();
        if (!response.ok) throw new Error(result);
        console.log('Product added:', result);
    } catch (error) {
        console.error('Failed to add product:', error);
        throw error;
    }
}

/**
 * Sends a request to update an existing product by ID.
 * @param id The ID of the product to be updated.
 * @param product Updated product data.
 * @returns A promise that resolves when the product is successfully updated, or throws an error on failure.
 */
export async function updateProduct(
    id: number,
    product: Product,
): Promise<void> {
    try {
        const response = await fetch(`${BASE_URL}/${id}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(product),
        });
        const result = await response.text();
        if (!response.ok) throw new Error(result);
        console.log(`Product with ID ${id} updated:`, result);
    } catch (error) {
        console.error(`Failed to update product with ID ${id}:`, error);
        throw error;
    }
}

/**
 * Sends a request to delete a product by ID.
 * @param id The ID of the product to be deleted.
 * @returns A promise that resolves when the product is successfully deleted, or throws an error on failure.
 */
export async function deleteProduct(id: number): Promise<void> {
    try {
        const response = await fetch(`${BASE_URL}/${id}`, {
            method: 'DELETE',
        });
        const result = await response.text();
        if (!response.ok) throw new Error(result);
        console.log(`Product with ID ${id} deleted:`, result);
    } catch (error) {
        console.error(`Failed to delete product with ID ${id}:`, error);
        throw error;
    }
}
