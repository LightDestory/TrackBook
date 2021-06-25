import express, { Request, Response } from 'express';
import {authCheck} from '../middlewares/authMiddleware';
import Knex from "knex";
import db from "../db/database"
import {learnBooks} from "../middlewares/learnBooksMiddleware";

export const userRoute = express.Router();

userRoute.use(authCheck, learnBooks)

userRoute.get('/:id', (req: Request, res: Response) => {
    let id = req.params.id;
    let con = db.getConnection();
    con.select(['readings.title', "readings.page_read", 'books.isbn'])
        .from('readings')
        .innerJoin('books','books.id','readings.book_id')
        .where('readings.user_id',id)
        .then((results: any[]) => {
            res.status(200).json({"status": "success", "result": results});
        });
});

userRoute.post('/:id', (req: Request, res: Response) => {
    let id = req.params.id;
    let con = db.getConnection();
    con('books')
        .select('isbn', "id")
        .then(( rows : any[]) => {
            con('readings')
                .del()
                .where("user_id", id)
                .then(() => {
                    if((Object.keys(req.body).length == 0)){
                        res.status(200).json({"status": "success", "result": "Library flushed!"});
                        return;
                    }
                    let ids = new Map();
                    rows.forEach((row : any) => {ids.set(row.isbn, row.id)});
                    let data = req.body.map((book: any) => ({"user_id": id, "book_id": ids.get(book.isbn), "page_read": book.page_read, "title": book.title}));
                    con('readings')
                        .insert(data)
                        .then(() =>{
                            res.status(200).json({"status": "success", "result": "Backup successfully"});
                        });
                });
        });
});