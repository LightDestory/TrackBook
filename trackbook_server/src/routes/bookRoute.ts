import express, {Request, Response, Router} from 'express';
import {authCheck} from '../middlewares/authMiddleware';
import {Sender} from "../utils/Sender";
import {getTitle} from "../controllers/bookController";

export const bookRoute: Router = express.Router();

bookRoute.use(authCheck);

bookRoute.get('/:isbn', (req: Request, res: Response) => {
    let book_isbn: string = req.params.isbn;
    if (book_isbn.length != 13) {
        Sender.getInstance().sendError(res, Sender.ERROR_TYPE_PARAMETER);
        return;
    }
    getTitle(book_isbn, res);
});