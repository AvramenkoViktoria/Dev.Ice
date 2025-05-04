import {useState, ChangeEvent, FormEvent} from 'react';
import {useNavigate} from 'react-router-dom';
import {registerAndLogin, fetchUser} from '../http/auth';
import {Customer} from '../http/dto';
import Cookies from 'js-cookie';

interface RegisterProps {
    setIsAuthenticated: (auth: boolean) => void;
    setIsAdmin: (admin: boolean) => void;
    setLoading: (loading: boolean) => void;
}

const Register: React.FC<RegisterProps> = ({
    setIsAuthenticated,
    setIsAdmin,
    setLoading,
}) => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState<Customer>({
        email: '',
        phoneNum: '',
        secondName: '',
        firstName: '',
        lastName: '',
        password: '',
    });

    const [error, setError] = useState<string>('');

    const validateEmail = (email: string): boolean => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@.]{2,}(?:\.[^\s@.]{2,})*$/;
        return emailRegex.test(email);
    };

    const validatePhoneNumber = (phone: string): boolean => {
        const phoneRegex = /^\+\d+$/;
        return phoneRegex.test(phone);
    };

    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setFormData((prevData) => ({...prevData, [name]: value}));
    };

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const {email, phoneNum, secondName, firstName, lastName, password} =
            formData;

        if (!validateEmail(email)) {
            setError('Invalid email format');
            return;
        }

        if (!validatePhoneNumber(phoneNum)) {
            setError(
                'Phone number must start with "+" and contain only digits',
            );
            return;
        }

        if (!secondName || !firstName || !lastName || !password) {
            setError('All fields are required');
            return;
        }

        setError('');
        try {
            const success = await registerAndLogin(formData);
            if (success) {
                setLoading(true);
                const user = await fetchUser();
                if (user) {
                    Cookies.set('userEmail', email, {expires: 7});
                    setIsAuthenticated(true);
                    setIsAdmin(user.roles.includes('ROLE_MANAGER'));
                    navigate('/home');
                } else {
                    setError('Login succeeded, but failed to fetch user data');
                }
            } else {
                setError('Registration or auto-login failed');
            }
        } catch (err) {
            console.error(err);
            setError('Unexpected error during registration');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h2>Register</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Email:</label>
                    <input
                        type='email'
                        name='email'
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>Phone Number:</label>
                    <input
                        type='text'
                        name='phoneNum'
                        value={formData.phoneNum}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>Second Name:</label>
                    <input
                        type='text'
                        name='secondName'
                        value={formData.secondName}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>First Name:</label>
                    <input
                        type='text'
                        name='firstName'
                        value={formData.firstName}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>Last Name:</label>
                    <input
                        type='text'
                        name='lastName'
                        value={formData.lastName}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type='password'
                        name='password'
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />
                </div>
                {error && <p style={{color: 'red'}}>{error}</p>}
                <button type='submit'>Register</button>
            </form>
        </div>
    );
};

export default Register;
