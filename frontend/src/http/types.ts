export interface Product {
    product_id: string;
    name: string;
    selling_price: number;
    quantity: number;
}

export interface Order {
    order_id: number;
    [key: string]: any;
}
