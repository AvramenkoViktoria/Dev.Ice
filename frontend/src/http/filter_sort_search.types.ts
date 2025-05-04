type SingleKeyObject<T> = {
    [K in keyof T]: {
        [P in K]: T[P];
    } & Partial<Record<Exclude<keyof T, K>, never>>;
}[keyof T];

export type FilterValue =
    | string
    | number
    | boolean
    | (string | number | boolean)[]
    | {from?: string | number; to?: string | number};

type SortOrder = 'ASC' | 'DESC';

export type SearchPayload = {
    filter?: {
        [key: string]: FilterValue;
    };
    search?: {
        [key: string]: string | number | boolean;
    };
    sort?: {
        [key: string]: SortOrder;
    };
};
