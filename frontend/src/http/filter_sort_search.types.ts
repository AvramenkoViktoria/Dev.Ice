type SingleKeyObject<T> = {
    [K in keyof T]: {
        [P in K]: T[P];
    } & Partial<Record<Exclude<keyof T, K>, never>>;
}[keyof T];

type FilterValue =
    | string
    | number
    | boolean
    | (string | number | boolean)[]
    | {from?: string | number; to?: string | number};

export type SearchPayload = {
    filter?: {
        [key: string]: FilterValue;
    };
    search?: SingleKeyObject<{
        [key: string]: string | number | boolean;
    }>;
    sort?: SingleKeyObject<{
        [key: string]: 'ASC' | 'DESC';
    }>;
};
