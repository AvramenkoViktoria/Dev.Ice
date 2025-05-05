interface Product {
    name: string;
    qty: number;
    price: number;
    sum: number;
    code: string;
}

interface Order {
    id: number;
    manager: string;
    status: string;
    total: number;
    payment: string;
    paid: boolean;
    orderDate: string;
    shipDate: string;
    mail: string;
    department: string;
    clientId: string;
    phone: string;
    lastName: string;
    firstName: string;
    products: Product[];
}

const orders: Order[] = [
    {
        id: 12345,
        manager: 'Катерина',
        status: 'Нове',
        total: 12345,
        payment: 'Післяплата НП',
        paid: true,
        orderDate: '14.02.2006',
        shipDate: '17.02.2006',
        mail: 'Нова Пошта',
        department: '92',
        clientId: '54321',
        phone: '+380992531210',
        lastName: 'Бублик',
        firstName: 'Світлана',
        products: [
            {
                name: 'Apple Watch SE 2 GPS',
                qty: 2,
                price: 9699,
                sum: 12345,
                code: '836168',
            },
            {
                name: 'MacBook Air 13 M4 (2025)',
                qty: 1,
                price: 51299,
                sum: 51299,
                code: '842447',
            },
            {
                name: 'MacBook Air 13 M4 (2025)',
                qty: 1,
                price: 51299,
                sum: 51299,
                code: '842447',
            },
        ],
    },
    {
        id: 12356,
        manager: 'Катерина',
        status: 'Нове',
        total: 12345,
        payment: 'Післяплата НП',
        paid: true,
        orderDate: '14.02.2006',
        shipDate: '17.02.2006',
        mail: 'Нова Пошта',
        department: '92',
        clientId: '54321',
        phone: '+380992531210',
        lastName: 'Бублик',
        firstName: 'Світлана',
        products: [
            {
                name: 'Apple Watch SE 2 GPS',
                qty: 2,
                price: 9699,
                sum: 12345,
                code: '836168',
            },
            {
                name: 'Apple Watch SE 2 GPS',
                qty: 2,
                price: 9699,
                sum: 12345,
                code: '836168',
            },
            {
                name: 'Apple Watch SE 2 GPS',
                qty: 2,
                price: 9699,
                sum: 12345,
                code: '836168',
            },
            {
                name: 'Apple Watch SE 2 GPS',
                qty: 2,
                price: 9699,
                sum: 12345,
                code: '836168',
            },
            {
                name: 'Apple Watch SE 2 GPS',
                qty: 2,
                price: 9699,
                sum: 12345,
                code: '836168',
            },
            {
                name: 'MacBook Air 13 M4 (2025)',
                qty: 1,
                price: 51299,
                sum: 51299,
                code: '842447',
            },
            {
                name: 'MacBook Air 13 M4 (2025)',
                qty: 1,
                price: 51299,
                sum: 51299,
                code: '842447',
            },
        ],
    },
];

export default orders;
