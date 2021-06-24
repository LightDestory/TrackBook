import express, { Request, Response } from 'express';
import {authCheck} from '../middlewares/authMiddleware';
import Knex from "knex";
import db from "../db/database"

export const bookRoute = express.Router();

bookRoute.use(authCheck)

bookRoute.get('/:isbn', (req: Request, res: Response) => {
    let book_isbn: string = req.params.isbn;
    if (book_isbn.length != 13) {
        res.status(400).json({"status": "error", "code": "1", "message": "Invalid request's parameter!"});
        return;
    }// @ts-ignore
    let con: Knex = db.getConnection();
    con.select('title')
        .from("books")
        .where("isbn", book_isbn)
        .then((rows: any) => {
            if (rows.length === 0) {
                res.status(404).json({"status": "error", "code": "2", "result": "I don't know this book!"});
            } else {
                res.status(200).json({"status": "success", "result": rows[0].title});
            }
        });
});