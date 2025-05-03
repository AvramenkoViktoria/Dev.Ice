const BASE_URL = 'http://localhost:8080/api/password';

/**
 * Sends a request to encode a raw password using BCrypt.
 * @param rawPassword The plain text password to encode.
 * @returns A promise resolving to the encoded password.
 */
export async function encodePassword(rawPassword: string): Promise<string> {
    try {
        const response = await fetch(`${BASE_URL}/encode`, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain',
            },
            body: rawPassword,
        });

        if (!response.ok) throw new Error(response.statusText);

        const data = await response.text(); // response is plain text, not JSON
        return data;
    } catch (error) {
        console.error('Failed to encode password:', error);
        throw error;
    }
}
