import { ComputedRef } from 'vue';

export interface X6Json {
	id: string;
	shape: string;
}

export interface X6NodeJson extends X6Json {
    cmpData?: { [key: string]: any }
}

export interface X6EdgeJson extends X6Json {
    source: X6EdgePortJson
    target: X6EdgePortJson
}

export interface X6EdgePortJson {
    cell: string,
    port: string
}


export interface X6GraphJson {
    nodes: X6NodeJson[];
    edges: X6EdgeJson[];
}

export interface x6Layout extends X6NodeJson {
    x: ComputedRef<number>
    y: ComputedRef<number>
    width: ComputedRef<number>
    height: ComputedRef<number>
}