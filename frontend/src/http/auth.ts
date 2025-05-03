/**
 * Sends a login request with email and password.
 * @param email The user's email.
 * @param password The user's raw (plain text) password.
 * @returns A promise resolving to the login result message.
 */
export async function login(email: string, password: string): Promise<string> {
    const params = new URLSearchParams();
    params.append('email', email);
    params.append('password', password);

    try {
        const response = await fetch('http://localhost:8080/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            credentials: 'include',
            body: params.toString(),
        });

        if (!response.ok) {
            const contentType = response.headers.get('Content-Type');

            let errorMessage = 'Login failed';
            if (contentType && contentType.includes('application/json')) {
                const errorData = await response.json();
                errorMessage = errorData.message || errorMessage;
            } else {
                errorMessage = await response.text();
            }

            throw new Error(errorMessage);
        }

        return 'Login successful!';
    } catch (err) {
        console.error('Login error:', err);
        throw new Error('An error occurred during login');
    }
}

export async function fetchUser(): Promise<{
    email: string;
    roles: string[];
} | null> {
    try {
        const response = await fetch('http://localhost:8080/api/auth/me', {
            credentials: 'include',
        });

        const contentType = response.headers.get('Content-Type');

        if (response.ok && contentType?.includes('application/json')) {
            return await response.json();
        }

        console.warn('Unexpected response:', await response.text());
        return null;
    } catch (err) {
        console.error('Auth check failed:', err);
        return null;
    }
}
