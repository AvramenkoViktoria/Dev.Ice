import {useState, ChangeEvent, FormEvent} from 'react';
import {useNavigate} from 'react-router-dom';
import {login, fetchUser} from '../http/auth';

interface LoginProps {
    setIsAuthenticated: (auth: boolean) => void;
    setIsAdmin: (admin: boolean) => void;
    setLoading: (loading: boolean) => void;
}

const Login: React.FC<LoginProps> = ({
    setIsAuthenticated,
    setIsAdmin,
    setLoading,
}) => {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const validateEmail = (email: string): boolean => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@.]{2,}(?:\.[^\s@.]{2,})*$/;
        return emailRegex.test(email);
    };

    const handleLogin = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (!validateEmail(email)) {
            setError('Invalid email format.');
            return;
        }

        if (!password) {
            setError('Please enter your password.');
            return;
        }

        setError('');
        setLoading(true);

        try {
            const result = await login(email, password);

            if (result === 'Login successful!') {
                const user = await fetchUser();
                if (user) {
                    setIsAuthenticated(true);
                    setIsAdmin(user.roles.includes('ROLE_MANAGER'));
                    navigate('/home');
                } else {
                    setError('Failed to fetch user data after login.');
                }
            } else {
                setError(result);
            }
        } catch (err) {
            console.error('Login error:', err);
            setError('An error occurred during login. Please try again later.');
        } finally {
            setLoading(false);
        }
    };

    const handleGoToRegister = () => {
        navigate('/register');
    };

    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <div>
                    <label>Email:</label>
                    <input
                        type='email'
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type='password'
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                {error && <p style={{color: 'red'}}>{error}</p>}
                <button type='submit'>Login</button>
                <button type='button' onClick={handleGoToRegister}>
                    Register
                </button>
            </form>
        </div>
    );
};

export default Login;
