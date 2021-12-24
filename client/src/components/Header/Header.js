import React, { Component } from 'react'
import './Header.css'

export default class Header extends Component {
    constructor (props){
        super(props)

        this.onLogOut = (event) => {
            console.log("Logged out")
            this.props.setToken("")
        }
    }

    render() {
        return (
            <div className='Header'>
                <div id='logoName'>Digital Bookstore</div>
                <div class='menu'>
                    {this.props.token ? <button id='logOutButton' onClick={this.onLogOut}>Log Out</button>: null}
                </div>
             
            </div>
        )
    }
}
