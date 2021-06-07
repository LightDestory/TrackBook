import express, { Request, Response } from 'express';

export const loginRoute = express.Router();

loginRoute.post('/', (req: Request, res: Response) => {
    res.sendStatus(200);
});