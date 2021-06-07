import express, { Request, Response } from 'express';
import {authCheck} from './middleware/authMiddleware';
export const userRoute = express.Router();

userRoute.use(authCheck)

userRoute.get('/:id', (req: Request, res: Response) => {
    let auth = req.query.auth;
    res.send({"id": req.params.id, "auth": auth});
});

userRoute.post('/:id', (req: Request, res: Response) => {
    let auth = req.body.auth;
    res.send({"id": req.params.id, "auth": auth});
});