import React from 'react';
import {Navigate} from 'react-router-dom';

interface ProtectedRouteProps {
    isAuthenticated: boolean;
    loading: boolean;
    children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
    isAuthenticated,
    loading,
    children,
}) => {
    if (loading) {
        return <div>Loading...</div>;
    }

    if (!isAuthenticated) {
        return <Navigate to='/' replace />;
    }

    return <>{children}</>;
};

export default ProtectedRoute;
