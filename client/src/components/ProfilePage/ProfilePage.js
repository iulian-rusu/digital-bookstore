import React, { Component } from 'react'
import './ProfilePage.css'

export default class ProfilePage extends Component {
    render() {
        return (
            <div class='ProfilePage flexColumn alignCenter'>
                <h1 id="greetingMessage">Welcome, {this.props.user.username}</h1>
           
            </div>
        )
    }
}
