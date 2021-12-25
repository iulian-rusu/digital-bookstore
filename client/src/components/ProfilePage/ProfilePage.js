import React, { Component } from 'react'
import './ProfilePage.css'

export default class ProfilePage extends Component {
    constructor(props) {
        super(props)

        this.state = {
            newPassword: "",
            confirmPassword: ""
        }

        this.renderRole = () => {
            switch(this.props.user.role) {
                case "ROLE_USER": return "User"
                case "ROLE_MANAGER": return "Manager"
                case "ROLE_ADMIN": return "Admin"
                default: return "User"
            }
        }

        this.setNewPassword = event => {
            this.state.newPassword = event.target.value
        }

        this.setConfirmPassword = event => {
            this.state.confirmPassword = event.target.value
        }

        this.submitForm = event => {
            event.preventDefault()
            // send API request to update user
        }
    }

    render() {
        return (
            <div className='ProfilePage flexColumn alignCenter'>
                <h1 id="greetingMessage">Welcome, {this.props.user.username}</h1>
                <form id="profileForm" className="flexColumn alignCenter" onSubmit={this.submitForm}>
                    <fieldset className="flexColumn alignCenter">
                    <legend>Personal Information</legend>
                        <div>
                            <label>Username</label>
                            <input type="text" disabled value={this.props.user.username} />
                        </div>
                        <div>
                            <label>Role</label>
                            <input type="text" disabled value={this.renderRole()} />
                        </div>
                        <div>
                            <label>New Password</label>
                            <input type="password" name="password" onChange={this.setNewPassword} />
                        </div>
                        <div>
                            <label>Confirm Password</label>
                            <input type="password" name="passwordConfirm" onChange={this.setConfirmPassword}/>
                        </div>
                    </fieldset>
                    <div id="formButtons">
                        <input type="submit" className='darkButton' value="Update" />
                    </div>
                </form>
            </div>
        )
    }
}
