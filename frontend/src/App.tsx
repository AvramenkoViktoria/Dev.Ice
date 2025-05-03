import {useEffect, useState} from 'react';
import {
    BrowserRouter as Router,
    Routes,
    Route,
    useLocation,
} from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Home from './components/Home';
import Admin from './components/Admin';
import ProtectedRoute from './components/ProtectedRoute';
import {fetchUser} from './http/auth';

const App = () => {
    return (
        <Router>
            <AppRoutes />
        </Router>
    );
};

const AppRoutes = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);
    const [loading, setLoading] = useState(true);
    const location = useLocation();

    useEffect(() => {
        const init = async () => {
            setLoading(true);
            try {
                const user = await fetchUser();
                if (user) {
                    setIsAuthenticated(true);
                    setIsAdmin(user.roles.includes('ROLE_MANAGER'));
                } else {
                    setIsAuthenticated(false);
                    setIsAdmin(false);
                }
            } catch (err) {
                console.error('Failed to fetch user', err);
                setIsAuthenticated(false);
                setIsAdmin(false);
            } finally {
                setLoading(false);
            }
        };

        init();
    }, [location.pathname]);

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <Routes>
            <Route
                path='/'
                element={
                    <Login
                        setIsAuthenticated={setIsAuthenticated}
                        setIsAdmin={setIsAdmin}
                        setLoading={setLoading}
                    />
                }
            />
            <Route path='/register' element={<Register />} />
            <Route
                path='/home'
                element={
                    <ProtectedRoute
                        isAuthenticated={isAuthenticated}
                        loading={loading}
                    >
                        <Home />
                    </ProtectedRoute>
                }
            />
            <Route
                path='/admin'
                element={
                    <ProtectedRoute
                        isAuthenticated={isAuthenticated && isAdmin}
                        loading={loading}
                    >
                        <Admin />
                    </ProtectedRoute>
                }
            />
        </Routes>
    );
};

export default App;
