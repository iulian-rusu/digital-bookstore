import React, { Component } from 'react'
import './App.css';
import Header from '../Header/Header';
import LoginPage from '../LoginPage/LoginPage';
import HomePage from '../HomePage/HomePage';

export default class App extends Component {
    constructor(props) {
        super(props)

        this.state = {
            token: ""
        }

        this.setToken = tk => {
            this.setState({ token: tk })
        }
    }

    render() {
        return (
            <div className="App">
                <Header token={this.state.token} setToken={this.setToken} />
                {
                    this.state.token
                        ? <HomePage token={this.state.token} />
                        : <LoginPage setToken={this.setToken} />
                }
            </div>
        )
    }
}
