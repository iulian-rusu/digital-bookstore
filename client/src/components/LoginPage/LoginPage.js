import React, { Component } from 'react'
import './LoginPage.css'

export default class LoginPage extends Component {

    constructor(props) {
        super(props)

        this.state = {
            username: "",
            password: "",
            errorMessage: ""
        }

        this.usernamePattern = /^[a-zA-Z_]\w+$/
        this.passwordPattern = /^.{4,}$/

        this.onUsernameChange = event => {
            this.setState({ username: event.target.value })
        }

        this.onPasswordChange = event => {
            this.setState({ password: event.target.value })
        }

        this.validateData = () => {
            if (!this.state.username.match(this.usernamePattern)) {
                this.setState({ errorMessage: "Invalid username format" })
                return false
            }
            if (!this.state.password.match(this.passwordPattern)) {
                this.setState({ errorMessage: "Invalid password format" })
                return false
            }
            return true
        }
        this.onLogIn = event => {
            if (this.validateData()) {
                this.props.setUser({
                    token: "TODO",
                    username: this.state.username,
                    role: "ROLE_USER"
                })
            }
            event.preventDefault()
            return false
        }
    }

    render() {
        return (
            <div className='LoginPage'>
                <h3 id="loginMessage">Please enter your username and password</h3>
                <form id="loginForm">
                    <input type="text" placeholder="username" autoComplete="off"
                        onChange={this.onUsernameChange} required />
                    <input type="password" placeholder="password" autoComplete="off"
                        onChange={this.onPasswordChange} required />
                    <button className="darkButton" onClick={this.onLogIn}>Log In</button>
                </form>
                {this.state.errorMessage ? <h1>{this.state.errorMessage}</h1> : null}
            </div>
        )
    }
}
