export type OrderRequestDto = {
    orderId?: number;
    managerId: number | null;
    customerEmail: string;
    status: string;
    placementDate: string;
    dispatchDate?: string | null;
    paymentMethod: string;
    payed: boolean;
    post: string;
    postOffice: string;
    orderAmount: number;
    products: {
        productId: number;
        number: number;
    }[];
};

export type Order = {
    orderId: number;
    managerId: number;
    customerEmail: string;
    status: string;
    placementDate: string;
    dispatchDate: string | null;
    paymentMethod: string;
    payed: boolean;
    post: string;
    postOffice: string;
    orderAmount: number;
};

export type OrderProduct = {
    orderId: number;
    productId: number;
    number: number;
};

export type OrderUpdateRequestDto = {
    order: Order;
    orderProducts: OrderProduct[];
};
