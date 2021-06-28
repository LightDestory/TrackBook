import express, {Request, Response, Router} from 'express';
import {changePassword, loginUser, registerUser} from "../controllers/accountController";
import {Sender} from "../utils/Sender";

export const accountRoute: Router = express.Router();

accountRoute.post('/register', (req: Request, res: Response) => {
    let email: string | undefined = req.body.email;
    let password: string | undefined = req.body.password;
    let penName: string | undefined = req.body.penName;
    if (!email || !password || !penName || password.length != 32) {
        Sender.getInstance().sendError(res, Sender.ERROR_TYPE_PARAMETER);
        return;
    }
    registerUser(email, password, penName, res);
});

accountRoute.post('/login', (req: Request, res: Response) => {
    let email: string | undefined = req.body.email;
    let password: string | undefined = req.body.password;
    if (!email || !password) {
        Sender.getInstance().sendError(res, Sender.ERROR_TYPE_PARAMETER);
        return;
    }
    loginUser(email, password, res);
});

accountRoute.post('/change', (req: Request, res: Response) => {
    let email: string | undefined = req.body.email;
    let new_password: string | undefined = req.body.password;
    let penName: string | undefined = req.body.penName;
    if (!email || !new_password || !penName || new_password.length != 32) {
        Sender.getInstance().sendError(res, Sender.ERROR_TYPE_PARAMETER);
        return;
    }
    changePassword(email, penName, new_password, res);
});