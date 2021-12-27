import React, { Component } from 'react'
import PastOrder from '../PastOrder/PastOrder'
import './PastOrdersList.css'

export default class PastOrdersList extends Component {
    constructor(props) {
        super(props)

        this.state = {
            pastOrders: [
                {
                    id: "ad9a0c-1dasida",
                    date: "09-10-2021",
                    isbnList: "11123123, 23123123123, 23123123",
                    status: "finalized"
                },
                {
                    id: "a323d9a0c-1dasida",
                    date: "09-08-2021",
                    isbnList: "33333, 55555555, 6666666, 11-23123-213, 09999999",
                    status: "cancelled"
                },      
                {
                    id: "ad9a0c-11232dasida",
                    date: "09-12-2021",
                    isbnList: "11123123",
                    status: "pending"
                }
            ]
        }
    }

    render() {
        return (
            <div className='PastOrdersList flexColumn alignCenter'>
                <h1>Past Orders</h1>
                <table className='styledTable'>
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Date</th>
                            <th>Items</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.state.pastOrders.map(
                            i => <PastOrder key={i.id} item={i} />
                        )}
                    </tbody>
                </table>
            </div>
        )
    }
}
