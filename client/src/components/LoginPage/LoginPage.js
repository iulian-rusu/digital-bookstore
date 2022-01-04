import React, { Component } from 'react'
import './LoginPage.css'
import {postAuth, extractAuthData} from '../../modules/SOAPRequest'

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
            event.preventDefault()

            if (this.validateData()) {
                // const response = postAuth(this.state.username, this.state.password)
                // if (!response.ok) {
                //     this.setState({ errorMessage: "Cannot authenticate" })
                //     return
                // }

                // const userData = extractUserData(response)
                const userData = {
                    token: "...",
                    userId: 2,
                    role: "ROLE_USER"
                }
                this.props.setUser({
                    token: userData.token,
                    userId: userData.userId,
                    username: this.state.username,
                    role: userData.role
                })
            }
        }
    }

    render() {
        return (
            <div className='LoginPage flexColumn alignCenter'>
                <h3 id="loginMessage">Please log in to your account</h3>
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
