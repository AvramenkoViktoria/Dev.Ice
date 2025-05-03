import {Navigate, useLocation} from 'react-router-dom';

interface ProtectedRouteProps {
    isAuthenticated: boolean;
    children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
    isAuthenticated,
    children,
}) => {
    const location = useLocation();

    if (!isAuthenticated) {
        return <Navigate to='/' state={{from: location}} />;
    }

    return <>{children}</>;
};

export default ProtectedRoute;
