import express, { Request, Response } from 'express';
import {authCheck} from '../middlewares/authMiddleware';
import {learnBooks} from "../middlewares/learnBooksMiddleware";
import {getUserData, updateUserData} from "../controllers/userController";

export const userRoute = express.Router();

userRoute.use(authCheck, learnBooks)

userRoute.get('/', (req: Request, res: Response) => {
    let id : string | undefined = req.headers.authorization?.split("-")[0]
    if(id)
        getUserData(id, res);
});

userRoute.post('/', (req: Request, res: Response) => {
    let id : string | undefined = req.headers.authorization?.split("-")[0]
    if(id)
        updateUserData(id, req.body.books, res);
});