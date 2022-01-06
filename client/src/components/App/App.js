import React, {Component} from 'react'
import Header from '../Header/Header';
import LoginPage from '../LoginPage/LoginPage';
import BooksPage from '../BooksPage/BooksPage';
import CartPage from '../CartPage/CartPage';
import ProfilePage from '../ProfilePage/ProfilePage';

export default class App extends Component {
    constructor(props) {
        super(props)

        const savedUser = localStorage.getItem("pos.bookstore.user")
        this.state = {
            user: savedUser,
            currentOrder: [],
            currentPage: "books"
        }

        this.setUser = u => {
            if (!u || !u.token) {
                this.state.currentPage = "books"
            }
            if (u) {
                localStorage.setItem("pos.bookstore.user", u)
            } else {
                localStorage.removeItem("pos.bookstore.user")
            }

            this.setState({ user: u })
        }

        this.updateUserToken = token => {
            this.state.user.token = token
        }

        this.orderItem = book => {
            const existing = this.state.currentOrder.find(b => b.isbn === book.isbn)
            if (existing) {
                existing.quantity = book.quantity
                this.setState({ currentOrder: this.state.currentOrder })
                return
            }

            const orderedItem = {
                isbn: book.isbn,
                title: book.title,
                quantity: book.quantity,
                price: book.price
            }
            this.setState({ currentOrder: [...this.state.currentOrder, orderedItem] })
        }

        this.clearCurrentOrder = () => {
            this.setState({
                currentOrder: []
            })
        }

        this.removeOrderedItem = isbn => {
            const remaining = this.state.currentOrder.filter(b => b.isbn !== isbn)
            this.setState({ currentOrder: remaining })
        }

        this.setCurrentPage = page => {
            this.setState({ currentPage: page })
        }

        this.getCurrentPage = () => {
            if (!this.state.user) {
                return <LoginPage setUser={this.setUser} />
            }

            const currentPage = this.state.currentPage
            switch (currentPage) {
                case "books": return <BooksPage user={this.state.user} orderItem={this.orderItem} />
                case "profile": return <ProfilePage user={this.state.user} updateUserToken={this.updateUserToken} />
                case "cart": return <CartPage
                    user={this.state.user}
                    currentOrder={this.state.currentOrder}
                    removeOrderedItem={this.removeOrderedItem}
                    clearCurrentOrder={this.clearCurrentOrder}
                />
                default: return <div>Unknown Page</div>
            }
        }
    }

    render() {
        return (
            <div className="App flexColumn">
                <Header user={this.state.user} setUser={this.setUser} setCurrentPage={this.setCurrentPage} />
                {this.getCurrentPage()}
            </div>
        )
    }
}
