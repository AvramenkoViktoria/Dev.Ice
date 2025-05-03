import {BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Home from './components/Home';
import Admin from './components/Admin';
import ProtectedRoute from './components/ProtectedRoute';

const App = () => {
    const isAuthenticated = localStorage.getItem('authToken') !== null;
    const isAdmin = localStorage.getItem('isAdmin') === 'true';

    return (
        <Router>
            <Routes>
                {/* Login */}
                <Route path='/' element={<Login />} />
                <Route path='/register' element={<Register />} />

                {/* Protected routes */}
                <Route
                    path='/home'
                    element={
                        <ProtectedRoute isAuthenticated={isAuthenticated}>
                            <Home />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path='/admin'
                    element={
                        <ProtectedRoute
                            isAuthenticated={isAuthenticated && isAdmin}
                        >
                            <Admin />
                        </ProtectedRoute>
                    }
                />
            </Routes>
        </Router>
    );
};

export default App;
