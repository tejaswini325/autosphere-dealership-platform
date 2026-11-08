import axios from 'axios';
export const api=axios.create({baseURL:import.meta.env.VITE_API_BASE_URL||'http://localhost:8080/api'});
api.interceptors.request.use(c=>{const token=localStorage.getItem('autos_token');if(token)c.headers.Authorization=`Bearer ${token}`;return c;});
export type Vehicle={id:number;stockId:string;type:'NEW'|'USED';model:string;variant:string;fuel:string;year:number;kilometres:number;exShowroomPrice:number;status:string;imageUrl:string;description:string};
export type Lead={id:number;name:string;email:string;phone:string;message:string;vehicleModel:string;status:string;assignedTo:string;createdAt:string};
