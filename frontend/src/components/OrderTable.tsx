import React, {useState, useEffect} from 'react';
import '../styles/OrderTable.css';
import {Order} from '../http/types';
import {SearchPayload} from '../http/filter_sort_search.types';
import {searchOrders, updateOrder, getOrderById} from '../http/order';
import {OrderUpdateRequestDto} from '../http/dto';

const managers = ['Катерина', 'Валентина', 'Олег'];
const statuses = ['Нове', 'В обробці', 'Відправлено', 'Завершено', 'Повернено'];

interface Column {
    key: keyof Order;
    label: string;
    editable?: boolean;
}

const columns: Column[] = [
    {key: 'order_id', label: 'ID'},
    {key: 'manager', label: 'Менеджер', editable: true},
    {key: 'status', label: 'Статус', editable: true},
    {key: 'order_amount', label: 'Сума'},
    {key: 'payment_method', label: 'Спосіб оплати'},
    {key: 'payed', label: 'Сплачено'},
    {key: 'placement_date', label: 'Дата оформлення'},
    {key: 'dispatch_date', label: 'Дата відправки', editable: true},
    {key: 'post', label: 'Пошта', editable: true},
    {key: 'post_office', label: 'Відділення', editable: true},
    {key: 'products', label: 'Товари'},
    {key: 'customer_id', label: 'ID клієнта'},
    {key: 'phone', label: 'Телефон', editable: true},
    {key: 'customer_lastname', label: 'Прізвище', editable: true},
    {key: 'customer_firstname', label: 'Ім’я', editable: true},
];

interface OrderTableProps {
    orders: Order[];
    onOpen: (order: Order) => void;
    onDeleteSelection?: () => void;
    selectedOrderIds: number[];
    setSelectedOrderIds: React.Dispatch<React.SetStateAction<number[]>>;
    resetSort?: boolean;
}

const OrderTable: React.FC<OrderTableProps> = ({
    onOpen,
    onDeleteSelection,
    selectedOrderIds,
    setSelectedOrderIds,
    resetSort,
}) => {
    const [orders, setOrders] = useState<Order[]>([]);
    const [editingCell, setEditingCell] = useState<{
        rowId: number;
        key: keyof Order;
    } | null>(null);
    const [tempValue, setTempValue] = useState<string>('');
    const [validationError, setValidationError] = useState<string>('');
    const [clientToDelete, setClientToDelete] = useState<number | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            const payload: SearchPayload = {
                search: {},
                filter: {},
                sort: {},
            };
            const result = await searchOrders(payload);
            if (result) {
                const mapped = result.map(
                    (item: any): Order => ({
                        order_id: item.order_id,
                        customer_id: item.customer_id,
                        status: item.status,
                        placement_date: item.placement_date,
                        order_amount: item.order_amount,
                        payment_method: item.payment_method,
                        post: item.post,
                        manager: item.manager,
                        payed: item.payed,
                        post_office: item.post_office,
                        phone: item.customer_phone,
                        customer_lastname: item.customer_lastname,
                        customer_firstname: item.customer_firstname,
                        dispatch_date: item.dispatch_date,
                    }),
                );
                setOrders(mapped);
            }
        };

        fetchData();
    }, []);

    const editableWithValidation: (keyof Order)[] = [
        'phone',
        'firstName',
        'lastName',
        'department',
    ];

    const validateValue = (key: keyof Order, value: string): boolean => {
        switch (key) {
            case 'department':
                return /^\d+$/.test(value);
            case 'phone':
                return /^\+?\d{10,15}$/.test(value);
            case 'firstName':
            case 'lastName':
                return /^[А-Яа-яЇїІіЄєҐґA-Za-z'-]{2,}$/u.test(value);
            default:
                return true;
        }
    };

    const handleCheckboxChange = (id: number) => {
        setSelectedOrderIds((prev) =>
            prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id],
        );
    };

    const renderChoice = (order: Order, col: Column) => {
        const isEditing =
            editingCell?.rowId === order.order_id &&
            editingCell?.key === col.key;

        if (isEditing) {
            const handleBlur = () => setEditingCell(null);
            const handleChange = (
                e: React.ChangeEvent<HTMLSelectElement | HTMLInputElement>,
            ) => {
                order[col.key] = e.target.value as any;
                setEditingCell(null);
            };

            if (col.key === 'manager') {
                return (
                    <select
                        autoFocus
                        value={order.manager}
                        onChange={handleChange}
                        onBlur={handleBlur}
                    >
                        {managers.map((m) => (
                            <option key={m} value={m}>
                                {m}
                            </option>
                        ))}
                    </select>
                );
            }

            if (col.key === 'status') {
                return (
                    <select
                        autoFocus
                        value={order.status}
                        onChange={handleChange}
                        onBlur={handleBlur}
                    >
                        {statuses.map((s) => (
                            <option key={s} value={s}>
                                {s}
                            </option>
                        ))}
                    </select>
                );
            }

            if (col.key === 'dispatch_date') {
                return (
                    <input
                        type='date'
                        autoFocus
                        value={order.shipDate}
                        onChange={handleChange}
                        onBlur={handleBlur}
                    />
                );
            }

            if (col.key === 'post') {
                return (
                    <select
                        autoFocus
                        value={order.mail}
                        onChange={handleChange}
                        onBlur={handleBlur}
                    >
                        {['Нова Пошта', 'УкрПошта', 'Meest'].map((m) => (
                            <option key={m} value={m}>
                                {m}
                            </option>
                        ))}
                    </select>
                );
            }
        }

        if (col.key === 'payed') return order.paid ? '✔️' : '';
        // if (col.key === 'products')
        //     return (
        //         <button className='open-button' onClick={() => onOpen(order)}>
        //             Відкрити
        //         </button>
        //     );

        if (col.key === 'customer_id') {
            return (
                <div style={{display: 'flex', justifyContent: 'space-between'}}>
                    <span>{order.clientId}</span>
                    <button
                        className='delete-icon'
                        title='Видалити клієнта'
                        onClick={(e) => {
                            e.stopPropagation();
                            setClientToDelete(order.clientId);
                        }}
                    >
                        🗑️
                    </button>
                </div>
            );
        }

        return order[col.key];
    };

    const renderCell = (order: Order, col: Column) => {
        const isEditing =
            editingCell?.rowId === order.order_id &&
            editingCell?.key === col.key;

        if (isEditing) {
            const handleBlur = async () => {};

            return (
                <>
                    <input
                        type='text'
                        autoFocus
                        value={tempValue}
                        onChange={(e) => setTempValue(e.target.value)}
                        onBlur={handleBlur}
                    />
                    {validationError && (
                        <div className='order-table-validation-error'>
                            {validationError}
                        </div>
                    )}
                </>
            );
        }

        return order[col.key];
    };

    return (
        <div className='order-table-container'>
            <table className='order-table-table'>
                <thead>
                    <tr>
                        <th></th>
                        {columns.map((col) => (
                            <th key={col.key}>{col.label}</th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {orders.map((order) => (
                        <tr key={order.order_id}>
                            <td>
                                <input
                                    type='checkbox'
                                    checked={selectedOrderIds.includes(
                                        order.order_id,
                                    )}
                                    onChange={() =>
                                        handleCheckboxChange(order.order_id)
                                    }
                                />
                            </td>
                            {columns.map((col) => (
                                <td
                                    key={col.key}
                                    onClick={() => {
                                        if (
                                            col.editable ||
                                            editableWithValidation.includes(
                                                col.key,
                                            )
                                        ) {
                                            setEditingCell({
                                                rowId: order.order_id,
                                                key: col.key,
                                            });
                                            setTempValue(
                                                order[col.key]?.toString() ||
                                                    '',
                                            );
                                            setValidationError('');
                                        }
                                    }}
                                >
                                    {[
                                        'manager',
                                        'status',
                                        'dispatch_date',
                                        'payed',
                                        'products',
                                        'post',
                                        'customer_id',
                                    ].includes(col.key as string)
                                        ? renderChoice(order, col)
                                        : renderCell(order, col)}
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>

            {clientToDelete !== null && (
                <div className='order-table-modal-overlay'>
                    <div className='order-table-modal-content'>
                        <p>
                            Ви дійсно хочете видалити клієнта з ID{' '}
                            {clientToDelete}?
                        </p>
                        <button onClick={() => setClientToDelete(null)}>
                            Так
                        </button>
                        <button onClick={() => setClientToDelete(null)}>
                            Ні
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default OrderTable;
