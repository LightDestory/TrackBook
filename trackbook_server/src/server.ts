import express, {Application} from 'express';
import bodyParser from "body-parser";
import {accountRoute} from "./routes/accountRoute";
import {userRoute} from "./routes/userRoute";
import {bookRoute} from "./routes/bookRoute";
import db from "./db/database"


const configuration = require('dotenv').config();
const app: Application = express();


function bootstrap() {
    if(configuration.error){
        console.error(`Unable to load environment settings. Error: \r\n${configuration.error}`);
        process.exit();
    }
    console.log("Loaded configuration!");
    db.isConnected()
        .then(() =>{
            console.log(`Connection with database established!`);
            app.use( express.json(), express.text(), bodyParser.urlencoded({extended: false}));
            app.use('/api/account', accountRoute);
            app.use('/api/user', userRoute);
            app.use('/api/book', bookRoute);
            let port = process.env.PORT || 5000;
            app.listen(port, () => {console.log(`Listening to ${port}!`)});
        })
        .catch(() => {
            console.error(`Unable to connect to the database!`);
            process.exit();
        });
}

bootstrap();