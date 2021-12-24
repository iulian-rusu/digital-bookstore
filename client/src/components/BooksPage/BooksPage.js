import React, { Component } from 'react'
import BookTable from '../BookTable/BookTable'
import './BooksPage.css'

export default class HomePage extends Component {
    render() {
        return (
            <div className='BooksPage'>
                <div id='searchDiv'>
                    <input type='text' id='searchInput' placeholder='search author ...'></input>
                    <button class='brightButton' id='searchButton'>Search</button>
                </div>
                <BookTable token={this.props.token}/>
            </div>
        )
    }
}
