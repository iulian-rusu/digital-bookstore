import React, { Component } from 'react'
import './App.css';
import Header from '../Header/Header';
import LoginPage from '../LoginPage/LoginPage';
import BooksPage from '../BooksPage/BooksPage';

export default class App extends Component {
    constructor(props) {
        super(props)

        this.state = {
            token: "",
            currentPage: "books"
        }

        this.setToken = tk => {
            if (!tk) {
                this.state.currentPage = "books"
            }
    
            this.setState({ token: tk })
        }

        this.setCurrentPage = page => {
            this.setState({ currentPage: page })
        }

        this.getCurrentPage = () => {
            const currentPage = this.state.currentPage

            if (!this.state.token) {
                return <LoginPage setToken={this.setToken} />
            }
            else if (currentPage === "books") {
                return <BooksPage token={this.state.token} />
            }
            else if (currentPage === "profile") {
                return <div>Profile</div>
            }
            return <div>Unknown Page</div>
        }
    }

    render() {
        return (
            <div className="App">
                <Header token={this.state.token} setToken={this.setToken} setCurrentPage={this.setCurrentPage} />
                {this.getCurrentPage()}
            </div>
        )
    }
}
