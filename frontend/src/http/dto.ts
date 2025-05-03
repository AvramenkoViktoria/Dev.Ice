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

export type Product = {
    productId?: number;
    saleId: number;
    name: string;
    sellingPrice: number;
    purchasePrice: number;
    category: string;
    inStock: boolean;
    storageQuantity: number;
    producer: string;
    brand: string;
    ram: number;
    color: string;
    country: string;
    prodYear: string;
    diagonal: number;
    internalStorage: number;
};

export type Customer = {
    email: string;
    phoneNum: string;
    secondName: string;
    firstName: string;
    lastName: string;
    password: string;
};

export type Sale = {
    saleId: number;
    name: string;
    discountValue: number;
};

export type Manager = {
    managerId: number;
    secondName: string;
    firstName: string;
    lastName: string;
    startDate: string;
    finishDate: string | null;
    phoneNum: string;
    email: string;
    password: string;
};
